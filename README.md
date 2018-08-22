## Navigator annotation processor.
### Introduction
Navigator is a android navigation library, a simplified version of [Dart](https://github.com/f2prateek/dart), with some new features: 
1. Navigation interceptor.
3. Scheme support.

### Guide
#### `@Navigation`
Annotate a Activity with: `@Navigation` 
``` Java
@Navigation
public class SecondActivity extends AppCompatActivity {
    ...
}
```
And the processor will generate a class `Navigator` in longest common parent package of all annotated `Activity`s. Call the following method to start a new instance of `SecondActivity`: 
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
The genereted method name is: `navigateTo`*ActivityName*

#### `@BundleExtra`
All fields of `Activity` annotated by `@BundleExtra` will have `setter`s in the return value of `navigateToXXX` method. You can set and pass them to target `Activity`. If any fields of `Activity` is annotated by `@BundleExtra`, the `@Navigation` annotation is optional. For example:
``` Java
public class SecondActivity extends AppCompatActivity {

    @BundleExtra
    String username;
    ...
}
```
Pass the `username` field as:
``` Java
Navigator.navigateToSecondActivity()
    .username("David")
    .startActivity(this);
```

To retieve the field values in the target `Activity`, please call `Navigator.bind` method:
``` Java
// in SecondActivity.onCreate(...)
Navigator.bind(this);
// this.username is set.
```

`@BundleExtra` support types:
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

#### Interceptor
`@Navigation` has a field named `interceptors`, it's list of `Class` which implements `Interceptor`. A new instance list of types in `interceptor` is created before navigating. The `intercept` method of `Interceptor` is called one by one until anyone of them returns `true`
``` Java
@Navigation(interceptors = LoginInterceptor.class)
public class SecondActivity extends AppCompatActivity {
    ...
}
```

An example of `Interceptor` implementation:
``` Java
public class LoginInterceptor implements Interceptor {
    // Called before building a Intent.
    @Override
    public boolean intercept(Context context) {
        if (Math.random() > 0.5) {
            return false;
        }
        Navigator.navigateToLoginActivity()
                .startActivity(context);
        return true;
    }

    // Called after a new Intent is created.
    // You can put extras to intent.
    @Override
    public boolean intercept(Intent intent) {
        return false;
    }
}
```

#### Scheme
Another field of `@Navigation` is `schemes`, you can add you custom schemes to this list. For example:
``` Java
@Navigation(schemes = {"example://third", "example://third2/:path", "http://www.example.com/third"})
public class ThirdActivity extends AppCompatActivity {
    ...
}
```
Warning：The scheme should contains **NO** query string(e.g. ?query=abc)

When navigating by scheme:
``` Java
Navigator.navigateTo(context, "example://third?query=abc");
```

Rules to extract argument from `url` is:
1. Every field annotated by `@BundleExtra` is mapped to a `key`
2. Default `key` is field name，e.g：key of `String username` is `"username"`，alternatively you can set a `key` by the `value` field of `@BundleExtra`. e.g. `@BundleExtra("n")` has a `key`: `"n"`
3. Any segment in scheme path start with `':'` and all query strings are put into a `(key, value)` map. The compiler try to extract all `value`s to related `key`s.

### Download
```
repositories {
    // Jcenter migration is applied.
    maven {
        url 'https://dl.bintray.com/thiross/mvn/'
    }
}
dependencies {
    implementation 'com.tutuur.android:navigator-annotation:0.9.1'
    annotationProcessor 'com.tutuur.android:navigator-compiler:0.9.1'
}
```
