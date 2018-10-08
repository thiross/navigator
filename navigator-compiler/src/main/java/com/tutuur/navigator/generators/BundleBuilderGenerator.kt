package com.tutuur.navigator.generators

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
     * @return true if [element] can put with a simple {@code put} method.
     */
    private fun isSimpleType(element: VariableElement): Boolean {
        val type = element.asType()
        return TypeName.get(type).isPrimitive ||
                env.isString(type) ||
                env.isDerivedFromSerializable(type) ||
                env.isDerivedFromParcelable(type)
    }

    /**
     * @return [TypeSpec] of bundle builder.
     * [fields] contain all fields of current class.
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
                        if (isSimpleType(element)) {
                            it.addStatement("put(\$S, this.\$N)", name, name)
                        }
                    }
                    .addStatement("return this")
                    .build())
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