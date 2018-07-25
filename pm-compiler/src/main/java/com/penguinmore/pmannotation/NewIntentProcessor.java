package com.penguinmore.pmannotation;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.penguinmore.pmannotation.NewIntent")
public class NewIntentProcessor extends AbstractProcessor {

    private Messager messager;

    private HashMap<String, String> activitiesWithPackage;

    private static final String METHOD_PREFIX = "start";
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private Filer filer;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        activitiesWithPackage = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(NewIntent.class)) {

            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                return true;
            }



            TypeElement typeElement = (TypeElement) element;
            activitiesWithPackage.put(
                    typeElement.getSimpleName().toString(),
                    elements.getPackageOf(typeElement).getQualifiedName().toString());
            messager.printMessage(Diagnostic.Kind.WARNING, "type Simple Name" + typeElement.getSimpleName().toString());
            messager.printMessage(Diagnostic.Kind.WARNING, "type qualified Name" + typeElement.getQualifiedName().toString());


        }
        generateNaviClass();

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> types = new LinkedHashSet<>();

        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {

            types.add(annotation.getCanonicalName());
        }

        return super.getSupportedAnnotationTypes();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(NewIntent.class);
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    private void generateNaviClass() {
        TypeSpec.Builder navigatorClass = TypeSpec
                .classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, String> element : activitiesWithPackage.entrySet()) {
            String activityName = element.getKey();
            String packageName = element.getValue();
            ClassName activityClass = ClassName.get(packageName, activityName);
            MethodSpec intentMethod = MethodSpec
                    .methodBuilder(METHOD_PREFIX + activityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(classIntent)
                    .addParameter(classContext, "context")
                    .addStatement("return new $T($L, $L)", classIntent, "context", activityClass + ".class")
                    .build();
            navigatorClass.addMethod(intentMethod);
        }

        try {
            JavaFile.builder("com.penguinmore.pmannotation", navigatorClass.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
