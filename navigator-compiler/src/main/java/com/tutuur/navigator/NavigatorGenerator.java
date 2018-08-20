package com.tutuur.navigator;

import android.content.Context;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tutuur.util.AnnotationProcessorHelper;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.tutuur.navigator.NavigationProcessor.FILE_COMMENT;

public class NavigatorGenerator {

    private static final String TAG = NavigatorGenerator.class.getSimpleName();

    private static final String CLASS_NAME = "Navigator";

    private static final String NAVIGATE_METHOD_FORMAT = "navigateTo%s";

    private final AnnotationProcessorHelper helper;

    private final String targetPackageName;

    private final Map<TypeElement, List<VariableElement>> navigation;

    NavigatorGenerator(AnnotationProcessorHelper helper, String targetPackageName, Map<TypeElement, List<VariableElement>> navigation) {
        this.helper = helper;
        this.targetPackageName = targetPackageName;
        this.navigation = navigation;
    }

    JavaFile brewJava() {
        return JavaFile.builder(targetPackageName, brewTypeSpec())
                .addFileComment(FILE_COMMENT)
                .build();
    }

    private TypeSpec brewTypeSpec() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC);
        for (TypeElement element : navigation.keySet()) {
            brewGotoMethod(builder, element);
            if (!navigation.get(element).isEmpty()) {
                brewBindMethod(builder, element);
            }
        }
        brewGotoUriMethod(builder);
        return builder.build();
    }

    private void brewGotoMethod(TypeSpec.Builder builder, TypeElement element) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(String.format(NAVIGATE_METHOD_FORMAT, element.getSimpleName()))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        String packageName = helper.getPackageName(element);
        String clazzName = String.format(BundleBuilderGenerator.CLASS_NAME_FORMAT, element.getSimpleName());
        ClassName clazz = ClassName.get(packageName, clazzName);
        methodBuilder.returns(clazz)
                .addStatement("return new $T()", clazz);
        builder.addMethod(methodBuilder.build());
    }

    private void brewGotoUriMethod(TypeSpec.Builder builder) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("navigateTo")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(TypeName.get(Context.class), "context")
                .addParameter(TypeName.get(String.class), "uri");
        for (TypeElement element : navigation.keySet()) {
            final Navigation navigation = element.getAnnotation(Navigation.class);
            if (navigation == null || navigation.schemes().length == 0) {
                continue;
            }
            ClassName clazz = ClassName.get(helper.getPackageName(element),
                    String.format(BundleBuilderGenerator.CLASS_NAME_FORMAT, element.getSimpleName()));
            methodBuilder.addStatement(String.format("$T _%s = $T.parse(uri)", element.getSimpleName()), clazz, clazz)
                    .beginControlFlow(String.format("if (_%s != null)", element.getSimpleName()))
                    .addStatement(String.format("_%s.startActivity(context)", element.getSimpleName()))
                    .addStatement("return true")
                    .endControlFlow();
        }
        methodBuilder.addStatement("return false");
        builder.addMethod(methodBuilder.build());
    }

    private void brewBindMethod(TypeSpec.Builder builder, TypeElement element) {
        ClassName clazz = ClassName.get(helper.getPackageName(element),
                String.format(BundleBuilderGenerator.CLASS_NAME_FORMAT, element.getSimpleName()));
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(TypeName.get(element.asType()), "target")
                .addStatement("$T.bind(target)", clazz);
        builder.addMethod(methodBuilder.build());
    }
}
