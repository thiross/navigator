## 路由工具类

### 使用指南
#### `@Navigation`
用来标注需要跳转的Activity比如：
``` Java
@Navigation
public class SecondActivity extends AppCompatActivity {
	...
}
```
`navigator-compiler`会在所有标记`@Navigation`类的最长公共pacakge下生成一个`Navigator`类，用以跳转，比如上例就可以用如下方法跳转到`SecondActivity`:
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

### 下载
```
dependencies {
    implementation 'com.tutuur.android:navigator-annotation:0.9.0'
    annotationProcessor 'com.tutuur.android:navigator-compiler:0.9.0'
}
```
