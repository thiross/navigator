package com.tutuur.navigator.generators

import android.content.Intent
import android.os.Bundle
import com.squareup.javapoet.*
import com.tutuur.compiler.extensions.*
import com.tutuur.navigator.BundleBuilder
import com.tutuur.navigator.BundleExtra
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

/**
 * A helper class to generate bundle builder class of [target] class.
 */
class BundleBuilderGenerator(private val target: TypeElement, private val env: ProcessingEnvironment) {

    /**
     * package name of the [target] element.
     */
    private val packageName = env.elements.packageNameOf(target)

    /**
     * @return [JavaFile] of bundle builder class.
     */
    fun brewJava(): JavaFile? {
        val type = target.asType()
        if (!env.isDerivedFromActivity(type) && !env.isDerivedFromFragment(type)) {
            env.e(TAG, "only activities or fragment supported.")
            return null
        }
        env.i(TAG, "Generating bundle builder for `${target.simpleName}`")
        return JavaFile.builder(packageName, brewType(fieldsOf(target), fieldsOfParents(target)))
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
     * @return {@code true} if [element] can put with a simple {@code put} method.
     */
    private fun isSimplePutType(element: VariableElement): Boolean {
        val type = element.asType()
        return TypeName.get(type).isPrimitive ||
                env.isString(type) ||
                env.isDerivedFromSerializable(type) ||
                env.isDerivedFromParcelable(type) ||
                env.isPrimitiveArray(type) ||
                env.isStringArray(type) ||
                env.isParcelableArray(type);
    }

    /**
     * @return [TypeSpec] of bundle builder. [fields] contain all fields of current class.
     * [parentFields] contain all derived fields.
     */
    private fun brewType(fields: List<Field>, parentFields: List<Field>): TypeSpec {
        // create type builder.
        val builder = TypeSpec.classBuilder(classNameOf(target))
                .superclass(ClassName.get(BundleBuilder::class.java))
                .addModifiers(Modifier.FINAL)
        // target bundle builder class.
        val targetType = ClassName.get(packageName, classNameOf(target))
        (parentFields + fields).forEach { field ->
            val element = field.element
            val type = TypeName.get(element.asType())
            val name = element.simpleName.toString()
            // add field declaration
            builder.addField(FieldSpec.builder(type, name).build())

            // add field setter.
            builder.addMethod(MethodSpec.methodBuilder(name)
                    .returns(targetType)
                    .addParameter(type, name)
                    .also {
                        if (isSimplePutType(element)) {
                            it.addStatement("put(\$S, this.\$N)", name, name)
                        }
                    }
                    .addStatement("return this")
                    .build())
        }
        if (fields.isNotEmpty()) {
            // create bind method.
            builder.addMethod(brewBindMethod(fields))
        }
        return builder.build()
    }

    /**
     * @return [MethodSpec] for {@code bind} method.
     */
    private fun brewBindMethod(fields: List<Field>): MethodSpec {
        val builder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(ClassName.get(target), "target")
                .returns(TypeName.VOID)
        if (env.isDerivedFromActivity(target.asType())) {
            builder
                    .addStatement("\$T intent = target.getIntent()", Intent::class.java)
                    .beginControlFlow("if (intent == null)")
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("\$T bundle = intent.getExtras()", Bundle::class.java)
        } else {
            builder.addStatement("\$T bundle = target.getArguments()", Bundle::class.java)
                    .beginControlFlow("if (bundle == null)")
                    .addStatement("return")
                    .endControlFlow()
        }
        fields.forEach { field ->
            val name = field.element.simpleName.toString()
            val type = field.element.asType()
            val clsName = field.element.rawClassName.capitalize()
            builder.beginControlFlow("if (bundle.containsKey(\$S))", name)
            if (isSimpleGetType(type)) {
                builder.addStatement("target.\$N = bundle.get\$N(\$S)", name, clsName, name)
            } else if (env.isDerivedFromParcelable(type)) {
                builder.addStatement("target.\$N = bundle.getParcelable(\$S)", name, name)
            } else if (env.isPrimitiveArray(type) || env.isStringArray(type)) {
                builder.addStatement("target.\$N = bundle.get\$NArray(\$S)", name, clsName, name);
            } else {
                env.e(TAG, "$type is not supported.")
            }
            builder.endControlFlow()
        }
        return builder.build()
    }

    companion object {
        /**
         * command line message tag.
         */
        private val TAG = BundleBuilderGenerator::class.simpleName!!


        /**
         * File comment of *_BundleBuilder.
         */
        private val FILE_COMMENT = """
            Auto generated code by Navigator library, do *NOT* modify!!!
        """.trimIndent()

        /**
         * @return companion bundle builder class name of [element].
         */
        fun classNameOf(element: TypeElement) = "${element.simpleName}_BundleBuilder"
    }
}

data class Field(val element: VariableElement, val annotation: BundleExtra)