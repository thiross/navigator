package com.tutuur.navigator.models

import com.squareup.javapoet.ClassName
import com.tutuur.navigator.Navigation
import com.tutuur.navigator.constants.Constants.BUNDLE_BUILDER_CLASS_SUFFIX
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
     * Navigation target class name.
     */
    val className = ClassName.get(type)!!

    /**
     * Bundle builder class name.
     */
    val builderName: ClassName =
            ClassName.get(packageName, "${element.simpleName}$BUNDLE_BUILDER_CLASS_SUFFIX")

    /**
     * [Navigation] annotation on [element]
     */
    private val navigation = element.getAnnotation(Navigation::class.java)

    /**
     * Uri scheme configurations.
     */
    val scheme = if (navigation == null) {
        Scheme.EMPTY
    } else {
        Scheme(navigation.page, navigation.subpage)
    }

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

    override fun equals(other: Any?) =
            if (other is NavigationTarget) {
                type.toString() == other.toString()
            } else {
                false
            }

    override fun hashCode() = type.toString().hashCode()
}