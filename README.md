# PMIntentAnnotation
[![](https://jitpack.io/v/PenguinK/PMIntentAnnotation.svg)](https://jitpack.io/#PenguinK/PMIntentAnnotation)

## 简介
PMIntentAnnnotation库可以帮助开发者轻松生成跳转Activity所需的Intent，只需要在对应的Activity类添加@NewIntent注解即可
## 使用方式
在项目顶部 build.gradle 文件中添加如下代码：

```allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
然后还需要修改对应module下的build.gradle文件，添加对应依赖
```
implementation 'com.github.PenguinK.PMIntentAnnotation:pm-annotation:v0.1'
annotationProcessor 'com.github.PenguinK.PMIntentAnnotation:pm-compiler:v0.1'
```

之后需要在对应Activity中添加@NewIntent
```
@NewIntent
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ...
    }
    ...

}
```
重新build项目，就会Navigator类以及
对应的以start为前缀的方法 返回值为Intent 我们可以Navigator.startXXX()方法获取Intent进行操作了。

