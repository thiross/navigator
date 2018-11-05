package com.tutuur.navigator.generators

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.Nullable
import com.squareup.javapoet.*
import com.tutuur.compiler.extensions.*
import com.tutuur.navigator.BundleExtra
import com.tutuur.navigator.Interceptor
import com.tutuur.navigator.constants.Constants.PAGE_FIELD_NAME
import com.tutuur.navigator.constants.Constants.PATTERN_FIELD_NAME
import com.tutuur.navigator.models.Comment.FILE_COMMENT
import com.tutuur.navigator.models.Field
import com.tutuur.navigator.models.NavigationTarget
import com.tutuur.navigator.models.Scheme
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.collections.ArrayList

/**
 * A helper class to generate intent builder class of [target] class.
 */
class IntentBuilderGenerator(private val target: NavigationTarget, private val env: ProcessingEnvironment) {

    /**
     * @return [JavaFile] of bundle builder class.
     */
    fun brewJava(): JavaFile? {
        if (!env.isDerivedFromActivity(target.type) && !env.isDerivedFromFragment(target.type)) {
            env.e(TAG, "only activities or fragment supported.")
            return null
        }
        env.i(TAG, "Generating bundle builder for `${target.builderName}`")
        return JavaFile.builder(target.packageName, brewType(fieldsOf(target.element), fieldsOfParents(target.element)))
                .addFileComment(FILE_COMMENT)
                .build()
    }

    /**
     * @return [Field]s of [element].
     */
    private fun fieldsOf(element: TypeElement): List<Field> {
        return element.enclosedElements.flatMap {
            if (it is VariableElement) {
                val annotation = it.getAnnotation(BundleExtra::class.java)
                if (annotation == null) {
                    listOf()
                } else {
                    listOf(Field(it, annotation))
                }
            } else {
                listOf()
            }
        }
    }

    /**
     * @return all parents' [Field]s of [element].
     */
    private fun fieldsOfParents(element: TypeElement): List<Field> {
        val baseType = element.superclass as? DeclaredType ?: return listOf()
        val baseElement = baseType.asElement() as? TypeElement ?: return listOf()
        return if (env.isActivity(baseType) || env.isFragment(baseType)) {
            listOf()
        } else {
            fieldsOfParents(baseElement) + fieldsOf(baseElement)
        }
    }

    /**
     * @return {@code true} if [type] can get with a simple {@code get*Extra} method.
     */
    private fun isSimpleGetType(type: TypeMirror): Boolean {
        return type.kind.isPrimitive ||
                env.isString(type)
    }

    /**
     * @return {@code true} if [type] can put with a simple {@code put} method.
     */
    private fun isSimplePutType(type: TypeMirror): Boolean {
        return type.kind.isPrimitive ||
                env.isString(type) ||
                env.isDerivedFromSerializable(type) ||
                env.isDerivedFromParcelable(type) ||
                env.isPrimitiveArray(type) ||
                env.isStringArray(type) ||
                env.isParcelableArray(type)
    }

    /**
     * @return [TypeSpec] of bundle builder. [fields] contain all fields of current class.
     * [parentFields] contain all derived fields.
     */
    private fun brewType(fields: List<Field>, parentFields: List<Field>): TypeSpec {
        // create type builder.
        val builder = TypeSpec.classBuilder(target.builderName)
                .superclass(ClassName.get("com.tutuur.navigator", "IntentBuilder"))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Bundle::class.java, "bundle")
                        .addStatement("super(bundle)")
                        .build())
        // create static pattern field.
        builder.addField(brewPageField(target.scheme))
        builder.addField(brewPatternField(target.scheme))
        // target bundle builder class.
        val allFields = parentFields + fields
        allFields.forEach { field ->
            val typeName = TypeName.get(field.type)
            val name = field.name
            // add field getter setter.
            builder.addMethod(MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(typeName)
                    .also {
                        val type = field.type
                        val component = field.component
                        when {
                            isSimpleGetType(type) ->
                                it.addStatement("return bundle.get\$N(\$S)", component, name)
                            env.isDerivedFromParcelable(type) ->
                                it.addStatement("return bundle.getParcelable(\$S)", name)
                            env.isPrimitiveArray(type) || env.isStringArray(type) ->
                                it.addStatement("return bundle.get\$NArray(\$S)", component, name)
                            env.isParcelableArray(type) ->
                                it.addStatement("\$T[] pa = bundle.getParcelableArray(\$S)", Parcelable::class.java, name)
                                        .beginControlFlow("if (pa == null)")
                                        .addStatement("return null")
                                        .endControlFlow()
                                        .addStatement("return \$T.copyOf(pa, pa.length, \$T.class)", Arrays::class.java, TypeName.get(type))
                            env.isStringList(type) ->
                                it.addStatement("return bundle.getStringArrayList(\$S)", name)
                            env.isParcelableList(type) ->
                                it.addStatement("return bundle.getParcelableArrayList(\$S)", name)
                        }
                    }
                    .build())
            builder.addMethod(MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(target.builderName)
                    .addParameter(typeName, "value")
                    .also {
                        when {
                            isSimplePutType(field.type) ->
                                it.addStatement("put(\$S, value)", name)
                            env.isStringList(field.type) ->
                                it.addStatement("putStringList(\$S, value)", name)
                            env.isParcelableList(field.type) ->
                                it.addStatement("putParcelableList(\$S, value)", name)
                            else ->
                                env.e(TAG, "${target.type}: ${field.name}(${field.type}) not supported.")
                        }
                    }
                    .addStatement("return this")
                    .build())
        }
        if (env.isDerivedFromActivity(target.type)) {
            builder.addMethod(brewBuildMethod())
        }
        if (fields.isNotEmpty()) {
            // create bind method.
            builder.addMethod(brewBindMethod(fields))
        }
        builder.addMethod(brewParseMethod(allFields))
        builder.addMethod(brewStartMethod(env.context, target.interceptors, false))
        builder.addMethod(brewStartMethod(env.activity, target.interceptors, true))
        builder.addMethod(brewStartMethod(env.fragment, target.interceptors, true))
        env.supportFragment?.let {
            builder.addMethod(brewStartMethod(it, target.interceptors, true))
        }
        return builder.build()
    }

    private fun brewPageField(scheme: Scheme): FieldSpec {
        if (scheme.page.contains('/')) {
            env.e(TAG, "${target.className}: `page' must not contains `/'")
        }
        if (scheme.subPage.startsWith('/')) {
            env.e(TAG, "${target.className}: `subpage' must not start with `/'")
        }
        val code = if (scheme.isEmpty()) {
            "null"
        } else {
            "\"${scheme.page}\""
        }
        return FieldSpec.builder(String::class.java, PAGE_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer(code)
                .build()
    }

    /**
     * create regex patterns for schemes.
     */
    private fun brewPatternField(scheme: Scheme): FieldSpec {
        val code = if (scheme.isEmpty()) {
            "null"
        } else {
            if (scheme.subPage.isEmpty()) {
                scheme.page
            } else {
                "${scheme.page}${scheme.subPage}"
            }.let {
                """Pattern.compile("${it.replace(Regex(":([^/]+)"), "(?<$1>[^/]+)")}")"""
            }
        }
        return FieldSpec.builder(Pattern::class.java, PATTERN_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(code)
                .build()
    }

    /**
     * `build` method create a [Intent] from [target] type if [target] is a activity.
     */
    private fun brewBuildMethod(): MethodSpec {
        return MethodSpec.methodBuilder("build")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context::class.java, "context")
                .returns(Intent::class.java)
                .addStatement("\$T intent = new Intent(context, \$T.class)", Intent::class.java, TypeName.get(target.type))
                .addStatement("intent.putExtras(bundle)")
                .addStatement("return intent")
                .build()
    }

    /**
     * @return [MethodSpec] for {@code bind} method.
     */
    private fun brewBindMethod(fields: List<Field>): MethodSpec {
        val builder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(Object::class.java, "object")
                .returns(TypeName.VOID)
                .addStatement("\$T target = (\$T)(object)", target.className, target.className)
        fields.forEach { field ->
            val name = field.name
            builder.beginControlFlow("if (bundle.containsKey(\$S))", name)
                    .addStatement("target.\$N = \$N()", name, name)
                    .endControlFlow()
        }
        return builder.build()
    }

    /**
     * Create startActivity methods. all classes in [interceptors] list will be instantiated and called before navigating.
     */
    private fun brewStartMethod(type: TypeMirror, interceptors: List<TypeMirror>, hasRequestCode: Boolean): MethodSpec {
        return MethodSpec.methodBuilder(if (hasRequestCode) "startActivityForResult" else "startActivity")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(TypeName.get(type), "context")
                .returns(TypeName.VOID)
                .also {
                    if (hasRequestCode) {
                        it.addParameter(TypeName.INT, "requestCode")
                    }
                    val context = if (env.isDerivedFromFragment(type)) "context.getActivity()" else "context"
                    if (interceptors.isNotEmpty()) {
                        it.addStatement("\$T<\$T> its = new \$T<>()", List::class.java, Interceptor::class.java, ArrayList::class.java)
                        interceptors.forEach { type ->
                            it.addStatement("its.add(new \$T())", TypeName.get(type))
                        }
                        it.beginControlFlow("for (\$T it : its)", Interceptor::class.java)
                                .beginControlFlow("if (it.preIntercept($context))")
                                .addStatement("return")
                                .endControlFlow()
                                .endControlFlow()
                    }
                    it.addStatement("\$T intent = build($context)", Intent::class.java)
                    if (interceptors.isNotEmpty()) {
                        it.beginControlFlow("for (\$T it : its)", Interceptor::class.java)
                                .beginControlFlow("if (it.intercept(intent))")
                                .addStatement("return")
                                .endControlFlow()
                                .endControlFlow()
                    }
                    if (hasRequestCode) {
                        it.addStatement("context.startActivityForResult(intent, requestCode)")
                    } else {
                        it.addStatement("context.startActivity(intent)")
                    }
                }
                .build()
    }

    /**
     * @return [MethodSpec] of `parse` method. This method compares all schemes to {@code scheme} parameter,
     * and returns `_IntentBuilder` object when matches.
     */
    private fun brewParseMethod(fields: List<Field>): MethodSpec {
        return MethodSpec.methodBuilder("parse")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addAnnotation(Nullable::class.java)
                .addParameter(String::class.java, "scheme")
                .returns(target.builderName)
                .addStatement("int index = scheme.indexOf(\"?\")")
                .addStatement("String main = index >= 0 ? scheme.substring(0, index) : scheme")
                .addStatement("\$T m = $PATTERN_FIELD_NAME.matcher(main)", Matcher::class.java)
                .beginControlFlow("if (!m.find())")
                .addStatement("return null")
                .endControlFlow()
                .also {
                    val fs = fields.filter { field ->
                        (field.isPrimitive || env.isString(field.type)) &&
                                (field.annotation.autowired || field.annotation.key != "[undefined]")
                    }
                    if (fs.isNotEmpty()) {
                        it.addStatement("\$T uri = \$T.parse(scheme)", Uri::class.java, Uri::class.java)
                        fs.forEach { field ->
                            val annotation = field.annotation
                            val key = if (annotation.key != "[undefined]") {
                                annotation.key
                            } else {
                                field.name
                            }
                            it.beginControlFlow("")
                                    .addStatement("String s")
                                    .beginControlFlow("try")
                                    .addStatement("s = m.group(\$S)", key)
                                    .nextControlFlow("catch(\$T e)", IllegalArgumentException::class.java)
                                    .addStatement("s = uri.getQueryParameter(\$S)", key)
                                    .endControlFlow()
                                    .beginControlFlow("if (s != null)")
                                    .also { _ ->
                                        if (field.isPrimitive) {
                                            it.beginControlFlow("try")
                                                    .addStatement("""${'$'}N(${when (field.type.kind) {
                                                        TypeKind.BOOLEAN -> "s.equalsIgnoreCase(\"true\") || s.equalsIgnoreCase(\"1\") || s.equalsIgnoreCase(\"t\")"
                                                        TypeKind.BYTE -> "Byte.parseByte(s)"
                                                        TypeKind.CHAR -> "s.charAt(0)"
                                                        TypeKind.SHORT -> "Short.parseShort(s)"
                                                        TypeKind.INT -> "Integer.parseInt(s)"
                                                        TypeKind.LONG -> "Long.parseLong(s)"
                                                        TypeKind.FLOAT -> "Float.parseFloat(s)"
                                                        TypeKind.DOUBLE -> "Double.parseDouble(s)"
                                                        else -> "undefined"
                                                    }})""", field.name)
                                                    .endControlFlow("catch(\$T e) {}", IllegalFormatException::class.java)
                                        } else {
                                            it.addStatement("\$N(s)", field.name)
                                        }
                                    }
                                    .endControlFlow()
                                    .endControlFlow()
                        }
                    }
                }.addStatement("return this")
                .build()
    }

    companion object {
        /**
         * command line message tag.
         */
        private val TAG = IntentBuilderGenerator::class.simpleName!!
    }
}
