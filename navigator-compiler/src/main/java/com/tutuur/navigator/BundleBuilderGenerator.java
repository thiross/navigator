package com.tutuur.navigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tutuur.util.AnnotationProcessorHelper;
import com.tutuur.util.TypeConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

import static com.tutuur.navigator.NavigationProcessor.FILE_COMMENT;

class BundleBuilderGenerator {

    private final String TAG = BundleBuilderGenerator.class.getSimpleName();

    public static final String CLASS_NAME_FORMAT = "%sBundleBuilder";

    private static final String EXTRA_KEY_FORMAT = "EXTRA_KEY_%s";

    private final AnnotationProcessorHelper helper;

    private final TypeElement clazz;

    private final List<VariableElement> members;

    private final String targetPackageName;

    private final String targetClassName;

    private final ClassName targetClassType;

    BundleBuilderGenerator(AnnotationProcessorHelper helper, TypeElement clazz, List<VariableElement> members) {
        this.helper = helper;
        this.clazz = clazz;
        this.members = members;
        this.targetPackageName = helper.getPackageName(clazz);
        this.targetClassName = String.format(CLASS_NAME_FORMAT, clazz.getSimpleName());
        this.targetClassType = ClassName.get(targetPackageName, targetClassName);
    }

    JavaFile brewJava() {
        if (clazz == null || members == null) {
            return null;
        }
        return JavaFile.builder(targetPackageName, brewType())
                .addFileComment(FILE_COMMENT)
                .build();
    }

    private TypeSpec brewType() {
        helper.i(TAG, String.format("Generating BundleBuilder: %s.%s", targetPackageName, targetClassName));
        TypeSpec.Builder builder = TypeSpec.classBuilder(targetClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        for (VariableElement member : members) {
            brewAttribute(builder, member);
        }
        brewBuildMethod(builder);
        brewNewIntentMethod(builder);
        brewStartActivityMethod(builder);
        brewStartActivityMethod(builder, TypeConstants.FQDN_ACTIVITY);
        brewStartActivityMethod(builder, TypeConstants.FQDN_FRAGMENT);
        brewStartActivityMethod(builder, TypeConstants.FQDN_SUPPORT_FRAGMENT);
        brewBindMethod(builder);
        brewParseMethod(builder);
        return builder.build();
    }

    private void brewAttribute(TypeSpec.Builder builder, VariableElement member) {
        final String name = member.getSimpleName().toString();
        final TypeName type = TypeName.get(member.asType());
        builder.addField(type, name, Modifier.PRIVATE);
        // add set method.
        builder.addMethod(MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, "value")
                .returns(targetClassType)
                .addStatement(String.format("this.%s = value", name))
                .addStatement("return this")
                .build());
    }

    private void brewBuildMethod(TypeSpec.Builder builder) {
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.get(Bundle.class))
                .addStatement("$T b = new $T()", Bundle.class, Bundle.class);
        for (VariableElement member : members) {
            final TypeMirror typeMirror = member.asType();
            final TypeName type = TypeName.get(typeMirror);
            final Name name = member.getSimpleName();
            final String key = String.format(EXTRA_KEY_FORMAT, name);
            if (type == TypeName.BOOLEAN) {
                methodBuilder.addStatement("b.putBoolean($S, this.$N)", key, name);
            } else if (type == TypeName.BYTE) {
                methodBuilder.addStatement("b.putByte($S, this.$N)", key, name);
            } else if (type == TypeName.CHAR) {
                methodBuilder.addStatement("b.putChar($S, this.$N)", key, name);
            } else if (type == TypeName.SHORT) {
                methodBuilder.addStatement("b.putShort($S, this.$N)", key, name);
            } else if (type == TypeName.INT) {
                methodBuilder.addStatement("b.putInt($S, this.$N)", key, name);
            } else if (type == TypeName.LONG) {
                methodBuilder.addStatement("b.putLong($S, this.$N)", key, name);
            } else if (type == TypeName.FLOAT) {
                methodBuilder.addStatement("b.putFloat($S, this.$N)", key, name);
            } else if (type == TypeName.DOUBLE) {
                methodBuilder.addStatement("b.putDouble($S, this.$N)", key, name);
            } else if (helper.isString(typeMirror)) {
                methodBuilder.addStatement("b.putString($S, this.$N)", key, name);
            } else if (helper.isStringArray(typeMirror)) {
                methodBuilder.addStatement("b.putStringArray($S, this.$N)", key, name);
            } else if (helper.isParcelable(typeMirror)) {
                methodBuilder.addStatement("b.putParcelable($S, this.$N)", key, name);
            } else if (helper.isSerializable(typeMirror)) {
                methodBuilder.addStatement("b.putSerializable($S, this.$N)", key, name);
            } else if (helper.isStringList(typeMirror)) {
                brewPutArrayListStatement(methodBuilder, key, name.toString(), String.class);
            } else if (helper.isParcelableList(typeMirror)) {
                brewPutArrayListStatement(methodBuilder, key, name.toString(), Parcelable.class);
            } else if (helper.isSerializable(typeMirror)) {
                brewPutArrayListStatement(methodBuilder, key, name.toString(), Serializable.class);
            } else {
                helper.e(TAG, String.format("putting type of `%s` in %s is not supported", name, clazz.getSimpleName()));
                return;
            }
        }
        methodBuilder.addStatement("return b");
        builder.addMethod(methodBuilder.build());
    }

    private void brewPutArrayListStatement(MethodSpec.Builder builder, String key, String name, Class clazz) {
        builder.beginControlFlow("if (this.$N != null)", name)
                .addStatement("$T<$T> l = new $T()", ArrayList.class, clazz, ArrayList.class)
                .addStatement("l.addAll(this.$N)", name)
                .addStatement(String.format("b.put%sArrayList($S, l)", clazz.getSimpleName()), key)
                .endControlFlow();
    }

    private void brewNewIntentMethod(TypeSpec.Builder builder) {
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("newIntent")
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.get(Intent.class))
                .addParameter(ClassName.get(Context.class), "context")
                .addStatement("$T intent = new $T(context, $T.class)", Intent.class, Intent.class, ClassName.get(clazz))
                .addStatement("intent.putExtras(build())")
                .addStatement("return intent");
        builder.addMethod(methodBuilder.build());
    }

    private void brewStartActivityMethod(TypeSpec.Builder builder) {
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startActivity")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(Context.class), "context")
                .addStatement("context.startActivity(newIntent(context))");
        builder.addMethod(methodBuilder.build());
    }

    private void brewStartActivityMethod(TypeSpec.Builder builder, String clazz) {
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startActivity")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(TypeName.get(helper.ofType(clazz)), "context")
                .addParameter(TypeName.INT, "requestCode");
        if (TypeConstants.FQDN_ACTIVITY.equals(clazz)) {
            methodBuilder.addStatement("context.startActivityForResult(newIntent(context), requestCode)");
        } else {
            methodBuilder.addStatement("context.startActivityForResult(newIntent(context.getContext()), requestCode)");
        }
        builder.addMethod(methodBuilder.build());
    }

    private void brewBindMethod(TypeSpec.Builder builder) {
        if (members.isEmpty()) {
            return;
        }
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(clazz), "target")
                .addStatement("Intent intent = target.getIntent()")
                .addStatement("if (intent == null) return");
        for (VariableElement member : members) {
            final TypeMirror typeMirror = member.asType();
            final TypeName type = TypeName.get(typeMirror);
            final Name name = member.getSimpleName();
            final String key = String.format(EXTRA_KEY_FORMAT, name);
            String postfix;
            if (type == TypeName.BOOLEAN) {
                postfix = "getBooleanExtra($S, false)";
            } else if (type == TypeName.BYTE) {
                postfix = "getByteExtra($S, (byte)0)";
            } else if (type == TypeName.CHAR) {
                postfix = "getCharExtra($S, (char)0)";
            } else if (type == TypeName.SHORT) {
                postfix = "getShortExtra($S, (short)0)";
            } else if (type == TypeName.INT) {
                postfix = "getIntExtra($S, 0)";
            } else if (type == TypeName.LONG) {
                postfix = "getLongExtra($S, 0L)";
            } else if (type == TypeName.FLOAT) {
                postfix = "getFloatExtra($S, 0f)";
            } else if (type == TypeName.DOUBLE) {
                postfix = "getDoubleExtra($S, 0.0)";
            } else if (helper.isString(typeMirror)) {
                postfix = "getStringExtra($S)";
            } else if (helper.isStringArray(typeMirror)) {
                postfix = "getStringArrayExtra($S)";
            } else if (helper.isParcelableArray(typeMirror)) {
                final TypeMirror elementType = ((ArrayType) typeMirror).getComponentType();
                methodBuilder.beginControlFlow("")
                        .addStatement("Parcelable[] ps = intent.getParcelableArrayExtra($S)", key)
                        .addStatement(String.format("target.%s = $T.copyOf(ps, ps.length, $T[].class)", name), Arrays.class, ClassName.get(elementType))
                        .endControlFlow();
                continue;
            } else if (helper.isParcelable(typeMirror)) {
                postfix = "getParcelableExtra($S)";
            } else if (helper.isSerializable(typeMirror)) {
                postfix = "getSerializableExtra($S)";
            } else if (helper.isStringList(typeMirror)) {
                postfix = "getStringArrayListExtra($S)";
            } else if (helper.isParcelableList(typeMirror)) {
                postfix = "getParcelableArrayListExtra($S)";
            } else if (helper.isSerializableList(typeMirror)) {
                postfix = "getSerializableArrayListExtra($S)";
            } else {
                helper.e(TAG, String.format("binding type of `%s` in %s is not supported", name, clazz.getSimpleName()));
                return;
            }
            methodBuilder.addStatement(String.format("target.%s = intent.%s", name, postfix), key);
        }
        builder.addMethod(methodBuilder.build());
    }

    private void brewParseMethod(TypeSpec.Builder builder) {
        Navigation navigation = clazz.getAnnotation(Navigation.class);
        if (navigation == null || navigation.schemes().length == 0) {
            return;
        }
        List<String> patterns = Lists.newArrayList();
        for (String scheme : navigation.schemes()) {
            helper.i(TAG, String.format("Processing scheme %s:`%s`", clazz.getSimpleName(), scheme));
            try {
                patterns.add(buildSchemePattern(scheme));
            } catch (Exception e) {
                helper.e(TAG, String.format("Failed to parse scheme %s:`%s`.", e.getMessage(), scheme));
            }
        }
        String code = String.format("{%s}", String.join(",", Lists.transform(patterns, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return String.format("Pattern.compile(\"(?i)%s\")", input);
            }
        })));
        builder.addField(FieldSpec.builder(ArrayTypeName.get(Pattern[].class), "PATTERNS")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(code)
                .build());
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("parse")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(targetClassType)
                .addParameter(ClassName.get(String.class), "uri")
                .beginControlFlow("for (Pattern p : PATTERNS)")
                .addStatement("$T m = p.matcher(uri)", Matcher.class)
                .beginControlFlow("if (!m.find())")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("$T b = new $T()", targetClassType, targetClassType);

        for (VariableElement member : members) {
            final BundleExtra extra = member.getAnnotation(BundleExtra.class);
            final String name = extra.value().equals("") ? member.getSimpleName().toString() : extra.value();
            methodBuilder.beginControlFlow("if (m.group($S) != null)", name)
                    .addStatement("String s = m.group($S)", name);
            final TypeName type = TypeName.get(member.asType());
            if (type == TypeName.BOOLEAN) {
                methodBuilder.addStatement(String.format("b.%s = s.equalsIgnoreCase(\"true\") || s.equalsIgnoreCase(\"1\")", member.getSimpleName()));
            } else if (type == TypeName.BYTE) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Byte.parseByte(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (type == TypeName.CHAR) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = s.charAt(0)", member.getSimpleName()))
                        .endControlFlow("catch(StringIndexOutOfBoundsException e) { }");
            } else if (type == TypeName.SHORT) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Short.parseShort(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (type == TypeName.INT) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Integer.parseInt(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (type == TypeName.LONG) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Long.parseLong(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (type == TypeName.FLOAT) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Float.parseFloat(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (type == TypeName.DOUBLE) {
                methodBuilder.beginControlFlow("try")
                        .addStatement(String.format("b.%s = Double.parseDouble(s)", member.getSimpleName()))
                        .endControlFlow("catch(NumberFormatException e) { }");
            } else if (helper.isString(member.asType())) {
                methodBuilder.addStatement(String.format("b.%s = s", member.getSimpleName()));
            }
            methodBuilder.endControlFlow();
        }
        methodBuilder.addStatement("return b")
                .endControlFlow()
                .addStatement("return null");
        builder.addMethod(methodBuilder.build());
    }

    private String buildSchemePattern(String scheme) {
        return scheme.replaceAll(":([^/]+)", "(?<$1>[^/]+)");
    }
}
