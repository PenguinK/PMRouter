# PMRouter
[![](https://jitpack.io/v/com.penguinmore/PMRouter.svg)](https://jitpack.io/#com.penguinmore/PMRouter)

## 简介
PMRouter 是一个android路由框架 目前已实现功能如下：
* Activity 路由
* Activity 参数自动注入
* 多module 生成路由表

## 使用方式

### 1. 基本配置

1. 在项目顶部 build.gradle 文件中添加如下代码：

```allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
2. 需要修改对应module下的build.gradle文件，添加对应依赖

```
implementation 'com.penguinmore.PMRouter:pm-annotation:{latest version}'//见顶部最新版本号 如v0.8
implementation 'com.penguinmore.PMRouter:pm-router:{latest version}'
//Java版本
annotationProcessor 'com.penguinmore.PMRouter:pm-compiler:{latest version}'
//Kotlin版本需要如下修改
apply plugin: 'kotlin-kapt'
kapt 'com.penguinmore.PMRouter:pm-compiler:{latest version}'
```

此外，还需要修改build.gradle，添加javaCompileOptions 的module参数

```
//Jave版本
android {
    .....
    defaultConfig {
        .....

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName() ]
            }
        }
    }
}
//Kotlin版本
kapt {
    arguments {
        arg("moduleName", project.getName())
    }
}
```

### 2. 实际使用

1. 在主module的自定义Application中初始化并注册要使用路由的module

```
 public class MyApp extends Application {
       @Override
       public void onCreate() {
           super.onCreate();
           Router.getInstance()
                   .init(new Config.Builder().
                   registerModules("app", "othermodule","kotlinmodule")
                   //registerModules方法传入要使用路由的module名称
                   .build());
       }
}
```

2. Activity跳转使用：在需要的Activity中加入@Router注解并设置path

```
@Route(path = "Main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        .....
    }
}
```
之后即可调用Router.getInstance().build({path}).navigation(context)跳转

```
Router.getInstance().
       build("Other").
       navigation(MainActivity.this);
```

跳转时还支持传递常见类型参数、设置Flag
* withString
* withBoolean
* withShort
* withInt
* withLong(
* withDouble
* withByte
* withChar
* withFloat
* withCharSequence
* withParcelable
* withParcelableArray
* withParcelableArrayList
* withSparseParcelableArray
* withIntegerArrayList
* withStringArrayList
* withCharSequenceArrayList
* withSerializable
* withByteArray
* withShortArray
* withCharArray
* withFloatArray
* withCharSequenceArray
* withBundle

```
//传递参数
Router.getInstance().
       build("Other").
       withString("id", "0089").
       navigation(MainActivity.this);
//设置flags
Router.getInstance().
       build("Other").
       setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).
       navigation(MainActivity.this);
```




