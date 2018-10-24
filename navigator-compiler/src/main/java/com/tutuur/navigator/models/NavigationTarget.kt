package com.tutuur.navigator.models

import com.squareup.javapoet.ClassName
import com.tutuur.navigator.Navigation
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * Model class for navigation targets. [element] should be a `Activity` or `Fragment`.
 */
data class NavigationTarget(val element: TypeElement) {

    /**
     * [TypeMirror] of [element]
     */
    val type: TypeMirror = element.asType()

    /**
     * Bundle builder class package name.
     */
    val packageName = run {
        assert(element.enclosingElement.kind == ElementKind.PACKAGE)
        (element.enclosingElement as PackageElement).qualifiedName.toString()
    }

    /**
     * Bundle builder class name.
     */
    val builderName: ClassName = ClassName.get(packageName, "${element.simpleName}_IntentBuilder")

    /**
     * [Navigation] annotation on [element]
     */
    private val navigation = element.getAnnotation(Navigation::class.java)

    /**
     * Uri scheme configurations.
     */
    val schemes = navigation?.schemes?.toList() ?: listOf()

    /**
     * interceptor class list.
     */
    val interceptors = element.annotationMirrors
            .find {
                it.annotationType.toString() == "com.tutuur.navigator.Navigation"
            }?.elementValues
            ?.entries
            ?.find {
                it.key.simpleName.toString() == "interceptors"
            }?.let {
                @Suppress("UNCHECKED_CAST")
                it.value.value as List<AnnotationValue>
            }?.map {
                it.value as TypeMirror
            }
            ?: listOf()

    companion object {
        /**
         * Scheme matching pattern array name.
         */
        const val PATTERN_ARRAY_NAME = "PATTERNS"
    }

    override fun equals(other: Any?) =
            if (other is NavigationTarget) {
                type.toString() == other.toString()
            } else {
                false
            }

    override fun hashCode() = type.toString().hashCode()
}