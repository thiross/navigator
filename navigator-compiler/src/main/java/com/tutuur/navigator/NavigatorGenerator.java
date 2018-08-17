package com.tutuur.navigator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tutuur.util.AnnotationProcessorHelper;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;

import static com.tutuur.navigator.NavigationProcessor.FILE_COMMENT;

public class NavigatorGenerator {

    private static final String TAG = NavigatorGenerator.class.getSimpleName();

    private static final String CLASS_NAME = "Navigator";

    private static final String GOTO_METHOD_FORMAT = "goto%s";

    private final AnnotationProcessorHelper helper;

    private final String packageName;

    private final Map<TypeElement, List<VariableElement>> navigation;

    NavigatorGenerator(AnnotationProcessorHelper helper, String packageName, Map<TypeElement, List<VariableElement>> navigation) {
        this.helper = helper;
        this.packageName = packageName;
        this.navigation = navigation;
    }

    JavaFile brewJava() {
        return JavaFile.builder(packageName, brewTypeSpec())
                .addFileComment(FILE_COMMENT)
                .build();
    }

    private TypeSpec brewTypeSpec() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC);
        for (TypeElement element : navigation.keySet()) {
            brewGotoMethod(builder, element);
        }
        return builder.build();
    }

    private void brewGotoMethod(TypeSpec.Builder builder, TypeElement element) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(String.format(GOTO_METHOD_FORMAT, element.getSimpleName()))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        String packageName = helper.getPackageName(element);
        String clazzName = String.format(BundleBuilderGenerator.CLASS_NAME_FORMAT, element.getSimpleName());
        ClassName clazz = ClassName.get(packageName, clazzName);
        methodBuilder.returns(clazz)
                .addStatement("return new $T()", clazz);
        builder.addMethod(methodBuilder.build());
    }
}
