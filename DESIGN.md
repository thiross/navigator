# 路由库设计方案
项目地址：[Navigator](https://github.com/thiross/navigator)
## 路由库的必要性
安卓App开发中，路由跳转是一个不可避免的业务需求，同时这部分代码一般都符合几个样式，很适合用自动代码生成的方式处理。一般路由库的实现都是用Annotation Processor、自定义Gradle Plugin或者两者结合的方式来生成路由代码。这里主要介绍下这几种方法的基本知识和优缺点。

## 单module路由跳转支持：Annotation Processor
Annotation Processor就是在编译过程中，通过给代码加Annotation的方式来自动生成路由代码，一般路由库都用这个方式来生成跳转代码，比如[Dart](https://github.com/f2prateek/dart)。一般通过对Activity增加一些注解，比如`@BundleExtra`等。在编译时可以通过AnntationProcessor获取被标记等类列表，然后根据类等结构类型`TypeElement`来生成自己需要等代码。具体可以参考`NavigatorProcessor`和`BundleBuilderGeneator`两个类，前者主要是通过`@AutoService`注解把`Processor`添加到编译流程，后者主要处理生成`__BundleBuilder`类。每个被标记等`Activity`都会生成一个`类名__BundleBuilder`的辅助类，用于`Activity`关联(方便在`new Intent(context, ActivityXXX.class)`的时候不需要显示传递`ActivityXXX.class`类型)和参数绑定。

参数绑定一般有两种形式，一个是[Dart](https://github.com/f2prateek/dart)方式：
``` java
Intent intent = HensonNavigator.gotoMyActivity(context)
 .extra1("foo")
 .extra2(42)
 .extra3(myObj) //optional
 .build()
```

还有一种方式是[WMRouter](https://github.com/meituan/WMRouter)方式：
``` java
new DefaultUriRequest(context, "/account")
        .putIntentExtra("test-int", 1)
        .putIntentExtra("test-string", "str")
        .start();
```

这两种方式的区别是，第一种方式导出了属性`extra1`等的setter函数，导致对目标跳转`Activity`有强依赖，不适合多modules等项目；而第二种方式牺牲了一点便捷性，比如需要手动传`"test-int"`这个extra key，但是解耦了对目标`Activity`的依赖，业务下沉，适合多modules等项目。

## 多module路由跳转支持：Annotation Processor + Gradle Plugin
一般Annotation Processor因为编译时只能对本module内的类进行处理，所以如果要路由库支持跨modules，那么必须借助其他计数，这里主要介绍下两种方式：
### Gradle task.
为支持多module [Dart](https://github.com/f2prateek/dart) 的方式是用如下方法增加一个自定义gradle task：
``` groovy
variant.registerJavaGeneratingTask(navigatorTask, destinationFolder)
```
`navigatorTask`作用时间是代码生成阶段，它会遍历所有依赖等`jar`文件，并筛选出所有`__IntentBuilder.class`结尾的class文件，并生成相应跳转代码。具体实现可以参考[GenerateHensonNavigatorTask.java](https://github.com/f2prateek/dart/blob/master/henson-plugin/src/main/java/dart/henson/plugin/internal/GenerateHensonNavigatorTask.java)。

### Android gradle plugin transformer.
[WMRouter](https://github.com/meituan/WMRouter)方式则更为自动化一点，他是利用了Android gradle plugin提供等Transform API在dex生成之前，遍历本地和依赖库等`jar`文件并找到所有`__IntentBuilder.class`类，在用[Java ASM](https://asm.ow2.io/)生成路由注册类的字节码，并在运行时用反射的方式找到注册类并完成注册。这个方法有个好处就是不用考虑`Dart`里生成跳转库等报名的问题。`Navigator`采用了这种方式，具体可以参看`NavigatorTransform`类来获取`__IntentBuilder.class`类名列表；`NavigatorServiceGenerator`用Java ASM来生成字节码。
