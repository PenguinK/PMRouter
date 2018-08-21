package com.penguinmore.pm_compiler;

import com.penguinmore.pm_annotation.InjectParam;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;


@SupportedAnnotationTypes("com.penguinmore.pm_annotation.InjectParam")
@SupportedOptions("moduleName")
public class InjectParamProcessor extends AbstractProcessor {


    private static final String PARAM_CLASS_SUFFIX = "$$InjectParam";
    private Messager mLogger;
    private Filer mFiler;
    private Elements mElements;
    private String mModuleName;
    private static final String MODULE_NAME = "moduleName";
    private Map<TypeElement, List<Element>> classAndParams = new HashMap<>();
    private final String TARGET = "target";
    private final String EXTRAS = "extras";
    private final String OBJ = "obj";
    public static final String ACTIVITY_FULL_NAME = "android.app.Activity";
    public final String METHOD_INJECT = "inject";
    public static final String INTERFACE_PACKAGE_NAME = "com.penguinmore.pm_router";
    public static final String PACKAGE_NAME = "com.penguinmore.router";
    public static final String PARAM_INJECT = "InjectionParam";
    public static final String PARAM_INJECT_FULL_NAME = INTERFACE_PACKAGE_NAME + ".template." + PARAM_INJECT;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mLogger = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mModuleName = processingEnv.getOptions().get(MODULE_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectParam.class);

        if (null == elements || elements.size() == 0) {
            return true;
        } else {
            classifyParams(elements);
            try {
                generateInjectClass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void generateInjectClass() throws IllegalAccessException, IOException {

        ParameterSpec objectParamSpec = ParameterSpec.builder(TypeName.OBJECT, OBJ).build();

        for (Map.Entry<TypeElement, List<Element>> entry : classAndParams.entrySet()) {
            TypeElement parent = entry.getKey();
            List<Element> params = entry.getValue();

            String qualifiedName = parent.getQualifiedName().toString();
            String simpleName = parent.getSimpleName().toString();
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            String fileName = simpleName + PARAM_CLASS_SUFFIX;

            // validate
            boolean isActivity;
            if (isSubtype(parent, ACTIVITY_FULL_NAME)) {
                isActivity = true;
            } else {
                throw new IllegalAccessException(
                        String.format("The target class %s must be Activity or Fragment.", simpleName));
            }


            // @Override
            // public void inject(Object obj) {}
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(METHOD_INJECT)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(objectParamSpec);

            // XXXActivity target = (XXXActivity) obj;
            injectMethodBuilder.addStatement("$T $L = ($T) $L",
                    ClassName.get(parent), TARGET, ClassName.get(parent), OBJ);
            if (isActivity) { // Bundle extras = target.getIntent().getExtras();
                injectMethodBuilder.addStatement("$T $L = $L.getIntent().getExtras()",
                        ClassName.get("android.os", "Bundle"), EXTRAS, TARGET);
            }

            for (Element param : params) {
                InjectParam injectParam = param.getAnnotation(InjectParam.class);
                String fieldName = param.getSimpleName().toString();
                String key = isEmpty(injectParam.name()) ? fieldName : injectParam.name();

                StringBuilder statement = new StringBuilder();

                Object[] args;
                // target.field = (FieldType) extras.getXXX("key"

                statement.append("$L.$L = ($T) $L.get")
                        .append(getAccessorType(param.asType())).append("(")
                        .append("$S");
                // , target.field
                if (supportDefaultValue(param.asType())) {
                    statement.append(", $L.$L");
                    args = new Object[]{TARGET, fieldName, ClassName.get(param.asType()), EXTRAS, key, TARGET, fieldName};
                } else {
                    args = new Object[]{TARGET, fieldName, ClassName.get(param.asType()), EXTRAS, key};
                }
                statement.append(")");

                injectMethodBuilder.addStatement(statement.toString(), args);

            }

            TypeElement interfaceType = processingEnv.getElementUtils().getTypeElement(PARAM_INJECT_FULL_NAME);
            TypeSpec typeSpec = TypeSpec.classBuilder(fileName)
                    .addSuperinterface(ClassName.get(interfaceType))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(injectMethodBuilder.build())
                    .addJavadoc(RouteProcessor.CLASS_JAVA_DOC)
                    .build();

            JavaFile.builder(packageName, typeSpec).build().writeTo(processingEnv.getFiler());

        }

    }

    private boolean supportDefaultValue(TypeMirror typeMirror) {
        if (typeMirror instanceof PrimitiveType) {
            return true;
        }
        if (isSubtype(typeMirror, "java.lang.String") || isSubtype(typeMirror, "java.lang.CharSequence")) {
            return true;
        }
        return false;
    }

    /**
     * Computes the string to append to 'get' or 'set' to get a valid Bundle method name.
     * For example, for the type int[], will return 'IntArray', which leads to the methods 'putIntArray' and 'getIntArray'
     *
     * @param typeMirror The type to access in the bundle
     * @return The string to append to 'get' or 'put'
     */
    private String getAccessorType(TypeMirror typeMirror) {
        if (typeMirror instanceof PrimitiveType) {
            return typeMirror.toString().toUpperCase().charAt(0) + typeMirror.toString().substring(1);
        } else if (typeMirror instanceof DeclaredType) {
            Element element = ((DeclaredType) typeMirror).asElement();
            if (element instanceof TypeElement) {
                if (isSubtype(element, "java.util.List")) { // ArrayList
                    List<? extends TypeMirror> typeArgs = ((DeclaredType) typeMirror).getTypeArguments();
                    if (typeArgs != null && !typeArgs.isEmpty()) {
                        TypeMirror argType = typeArgs.get(0);
                        if (isSubtype(argType, "java.lang.Integer")) {
                            return "IntegerArrayList";
                        } else if (isSubtype(argType, "java.lang.String")) {
                            return "StringArrayList";
                        } else if (isSubtype(argType, "java.lang.CharSequence")) {
                            return "CharSequenceArrayList";
                        } else if (isSubtype(argType, "android.os.Parcelable")) {
                            return "ParcelableArrayList";
                        }
                    }
                } else if (isSubtype(element, "android.os.Bundle")) {
                    return "Bundle";
                } else if (isSubtype(element, "java.lang.String")) {
                    return "String";
                } else if (isSubtype(element, "java.lang.CharSequence")) {
                    return "CharSequence";
                } else if (isSubtype(element, "android.util.SparseArray")) {
                    return "SparseParcelableArray";
                } else if (isSubtype(element, "android.os.Parcelable")) {
                    return "Parcelable";
                } else if (isSubtype(element, "java.io.Serializable")) {
                    return "Serializable";
                } else if (isSubtype(element, "android.os.IBinder")) {
                    return "Binder";
                }
            }
        } else if (typeMirror instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror compType = arrayType.getComponentType();
            if (compType instanceof PrimitiveType) {
                return compType.toString().toUpperCase().charAt(0) + compType.toString().substring(1) + "Array";
            } else if (compType instanceof DeclaredType) {
                Element compElement = ((DeclaredType) compType).asElement();
                if (compElement instanceof TypeElement) {
                    if (isSubtype(compElement, "java.lang.String")) {
                        return "StringArray";
                    } else if (isSubtype(compElement, "java.lang.CharSequence")) {
                        return "CharSequenceArray";
                    } else if (isSubtype(compElement, "android.os.Parcelable")) {
                        return "ParcelableArray";
                    }
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isSubtype(Element typeElement, String type) {
        return processingEnv.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnv.getElementUtils().getTypeElement(type).asType());
    }

    private void classifyParams(Set<? extends Element> elements) {
        for (Element element : elements) {
            // classify if element is field
            if (element.getKind().isField()) {

                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                //if already had same class
                if (classAndParams.containsKey(typeElement)) {
                    classAndParams.get(typeElement).add(element);
                } else {
                    List<Element> listElement = new ArrayList<>();
                    listElement.add(element);
                    classAndParams.put(typeElement, listElement);
                }

            }
        }


    }

    private boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }
    private boolean isSubtype(TypeMirror typeMirror, String type) {
        return processingEnv.getTypeUtils().isSubtype(typeMirror,
                processingEnv.getElementUtils().getTypeElement(type).asType());
    }

}
