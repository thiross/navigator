## 路由工具类
### 简介
Navigator参考了[Dart](https://github.com/f2prateek/dart)的实现，把Dart简化并增加了跳转拦截器和scheme支持。主要功能有：
1. scheme跳转并支持参数传递
2. 跳转拦截器

### 使用指南
#### `@Navigation` 和 `@BundleExtra`
`@Navigation`用来注解需要跳转的Activity比如：
``` Java
@Navigation(page = "second", subpage = ":userId")
public class SecondActivity extends AppCompatActivity {
    ...
}
```
其中`page`字段制定跳转用的`/page`页面，`subpage`用来解析页面参数。

`navigator-compiler`会在所有标记`@Navigation`或者属性有标记`@BundleExtra`类的package下生成一个`*__IntentBuilder`类，比如上例就会生成`SecondActivity__IntentBuilder`:
``` Java
public final class SecondActivity__IntentBuilder extends IntentBuilder {
    public static final String PAGE = "second";
    public static final String PATTERN = Pattern.compile("second/(?<userId>[^/]+)");
}
```

`@BundleExtra`用来注解类成员。例如：
``` Java
public class SecondActivity extends AppCompatActivity {

    @BundleExtra(autowired = true)
    String userId;
    ...
}
```
`navigator-plugin`会扫描依赖的库和当前module，生成一个(`*__IntentBuilder.PAGE`, `Constructor<? extends IntentBuilder>`)的map
跳转的时候可以通过如下方法传递`userId`给`SecondActivity`:
``` Java
Navigator.parse("/second/10001") // `second`作为key，在map里找到对应的`IntentBuilder`
    .startActivity(this);
// or
Navigator.parse("/second") // `second`作为key，在map里找到对应的`IntentBuilder`
    .put("userId", "10001")
    .startActivity(this);
```

`SecondActivity`需要在`Acitivity.onCreate`函数里用如下方法将`Intent`的Extra信息绑定到`Activity`：
``` Java
// in SecondActivity.onCreate(...)
Navigator.bind(this);
// this.userId == "10001".
```

目前@BundleExtra支持的类型有：
* `char`
* `byte`
* `short`
* `int`
* `long`
* `float`
* `double`
* `String`
* `Parcelable`
* `Serializable`
* `String[]`
* `Parcelable[]`
* `List<String>`
* `List<Parcelable>`
* `List<Serializable>`

#### 跳转拦截
`@Navigation`的`interceptors`成员可以指定跳转拦截器的`Class`对象列表，如：
``` Java
@Navigation(interceptors = LoginInterceptor.class)
public class SecondActivity extends AppCompatActivity {
    ...
}
```

这个列表的类必须实现`Interceptor`接口，如下简单的登录拦截器实现：
``` Java
public class LoginInterceptor implements Interceptor {

    @Override
    public boolean preIntercept(Context context) {
        return false;
    }

    @Override
    public boolean intercept(Intent intent) {
        if (Math.random() > 0.5) {
            return false;
        }
        Navigator.parse("/login")
            .put("targetIntent", intent)
            .startActivity(context);
        return true;
    }
}
```

#### 参数获取支持
`@Navigation`的`subpage`成员可以帮助url参数解析：
``` Java
@Navigation(page = "second", subpage = ":userId")
public class SecondActivity extends AppCompatActivity {
    @BundleExtra(autowired = true)
    String userId;
}
```
当需要根据url解析参数时，只需要:
``` Java
Navigator.parse("/second/10001")
    .startActivity(context); // 这里自动获取userId参数为10001
```
跳转的时候可以带query参数，并且参数将通过`Navigator.bind`绑定到对应的`@BundleExtra`标记的成员，对应规则如下：
1. 只对`@BundleExtra`标记为`autowired`或者设置了`key`的属性作处理；
2. `@BundleExtra`标记的成员默认参数名为成员名字，比如：`String username`的参数明为`"username"`，也可以通过`@BundleExtra(key = "n")`指定参数名为`"n"`；
3. 所有用`:`标记的路径变量和所有URL Query参数都会放到一个`Map`里面，然后根据1里面对应的参数名，从这个列表获取参数；

### 下载
Navigator compiler下载：
```
repositories {
    maven {
        jcenter()
    }
}
dependencies {
    implementation 'com.tutuur.android:navigator:0.9.13'
    annotationProcessor 'com.tutuur.android:navigator-compiler:0.9.13'
}
```

Navigator plugin下载：
```
buildscript {
    repositories {
        maven {
            jcenter()
        }
    }
    depdendencies {
        classpath 'com.tutuur.android:navigator-plugin:0.9.13'
    }
}
```