## 路由工具类
### 简介
Navigator参考了[Dart](https://github.com/f2prateek/dart)的实现，把Dart简化并增加了跳转拦截器和scheme支持。主要功能有：
1. Activity跳转并支持参数传递
2. scheme跳转并支持参数传递
3. 跳转拦截器

### 使用指南
#### `@Navigation`
`@Navigation`用来注解需要跳转的Activity比如：
``` Java
@Navigation
public class SecondActivity extends AppCompatActivity {
    ...
}
```

`navigator-compiler`会在所有标记`@Navigation`类的最长公共package下生成一个`Navigator`类，用以跳转，比如上例就可以用如下方法跳转到`SecondActivity`:
``` Java
Navigator.navigateToSecondActivity()
    .startActivity(context);
// or
Navigator.navigateToSecondActivity()
    .startActivity(context, requestCode);
// or
Navigator.navigateToSecondActivity()
    .startActivity(fragment, requestCode);
```
跳转类的方法名为`navigateToActivityName`

#### `@BundleExtra`
`@BundleExtra`用来注解类成员。含有`@BundleExtra`注解成员的Activity可以省略`@Navigation`注解。例如：
``` Java
public class SecondActivity extends AppCompatActivity {

    @BundleExtra
    String username;
    ...
}
```

跳转的时候可以通过如下方法传递`username`给`SecondActivity`:
``` Java
Navigator.navigateToSecondActivity()
    .username("David")
    .startActivity(this);
```
`SecondActivity`需要在`Acitivity.onCreate`函数里用如下方法将`Intent`的Extra信息绑定到`Activity`：
``` Java
// in SecondActivity.onCreate(...)
Navigator.bind(this);
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
    public boolean intercept(Context context) {
        if (Math.random() > 0.5) {
            return false;
        }
        Navigator.navigateToLoginActivity()
                .startActivity(context);
        return true;
    }

    @Override
    public boolean intercept(Intent intent) {
        return false;
    }
}
```

#### scheme支持
`@Navigation`的`schemes`成员可以制定scheme跳转，如：
``` Java
@Navigation(schemes = {"example://third", "example://third2/:path", "http://www.example.com/third"})
public class ThirdActivity extends AppCompatActivity {
    ...
}
```
注意：标记scheme时**不支持**query string(e.g. ?query=abc)，需要去掉

当需要根据scheme跳转只需要执行:
``` Java
Navigator.navigateTo(context, "example://third?query=abc");
```
跳转的时候可以带query参数，并且参数将通过`Navigator.bind`绑定到对应的`@BundleExtra`标记的成员，对应规则如下：
1. `@BundleExtra`标记的成员默认参数名为成员名字，比如：`String username`的参数明为`"username"`，也可以通过`@BundleExtra("n")`指定参数名为`"n"`。
2. 所有用`:`标记的路径变量和所有URL Query参数都会放到一个`Map`里面，然后根据1里面对应的参数名，从这个列表获取参数

### 下载
```
dependencies {
    implementation 'com.tutuur.android:navigator-annotation:0.9.1'
    annotationProcessor 'com.tutuur.android:navigator-compiler:0.9.1'
}
```
