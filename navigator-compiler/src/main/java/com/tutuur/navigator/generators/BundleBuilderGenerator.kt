package com.tutuur.navigator.generators

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import com.tutuur.compiler.extensions.*
import com.tutuur.navigator.BundleExtra
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

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
        if (!env.isDerivedFromActivity(target) && !env.isDerivedFromFragment(target)) {
            env.e(TAG, "only activities or fragment supported.")
            return null
        }
        env.i(TAG, "Generating bundle builder for `${target.simpleName}`")
        return JavaFile.builder(packageName, brewType(fieldsOf(target), fieldsOfParents(target)))
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
                    listOf(Field(it.simpleName.toString(), annotation))
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
        return if (env.isActivity(baseElement) || env.isFragment(baseElement)) {
            listOf()
        } else {
            fieldsOfParents(baseElement) + fieldsOf(baseElement)
        }
    }

    /**
     * @return [TypeSpec] of bundle builder.
     */
    private fun brewType(fields: List<Field>, parentFields: List<Field>): TypeSpec {
        val builder = TypeSpec.classBuilder(classNameOf(target))
        return builder.build()
    }

    companion object {
        /**
         * command line message tag.
         */
        private val TAG = BundleBuilderGenerator::class.simpleName!!

        /**
         * @return companion bundle builder class name of [element].
         */
        fun classNameOf(element: TypeElement) = "${element.simpleName}_BundleBuilder"
    }
}

data class Field(val name: String, val annotation: BundleExtra)