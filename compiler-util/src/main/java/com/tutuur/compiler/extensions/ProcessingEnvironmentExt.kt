package com.tutuur.compiler.extensions

import com.tutuur.compiler.types.Fqdn
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * @return [Elements] utilities of [ProcessingEnvironment].
 */
val ProcessingEnvironment.elements: Elements
    get() = elementUtils

/**
 * @return [Types] utilities of [ProcessingEnvironment].
 */
val ProcessingEnvironment.types: Types
    get() = typeUtils

/**
 * print [tag] and [message] at level [kind].
 */
private fun ProcessingEnvironment.print(kind: Diagnostic.Kind, tag: String, message: String) {
    messager.printMessage(kind, "[$tag] $message")
}

/**
 * print info [tag] and [message] to output.
 */
fun ProcessingEnvironment.i(tag: String, message: String) {
    print(Diagnostic.Kind.NOTE, tag, message)
}

/**
 * print warning [tag] and [message] to output.
 */
fun ProcessingEnvironment.w(tag: String, message: String) {
    print(Diagnostic.Kind.WARNING, tag, message)
}

/**
 * print error [tag] and [message] to output.
 */
fun ProcessingEnvironment.e(tag: String, message: String) {
    print(Diagnostic.Kind.ERROR, tag, message)
}

/**
 * @return {@code true} if [element] is [String]
 */
fun ProcessingEnvironment.isString(element: TypeElement): Boolean {
    return types.isSameType(element.asType(), elements.mirrorOf(Fqdn.STRING))
}

/**
 * @return {@code true} if [element] is exactly Activity class.
 */
fun ProcessingEnvironment.isActivity(element: TypeElement): Boolean {
    return types.isSameType(element.asType(), elements.mirrorOf(Fqdn.ACTIVITY))
}

/**
 * @return {@code true} if [element] is exactly Fragment class.
 */
fun ProcessingEnvironment.isFragment(element: TypeElement): Boolean {
    val type = element.asType()
    return types.isSameType(type, elements.mirrorOf(Fqdn.FRAGMENT)) || run {
        val fragmentElement = elements.getTypeElement(Fqdn.SUPPORT_FRAGMENT)
        if (fragmentElement == null) {
            false
        } else {
            types.isSameType(type, fragmentElement.asType())
        }
    }
}

/**
 * @return {@code true} if [element] is derived from Android `Activity`
 */
fun ProcessingEnvironment.isDerivedFromActivity(element: TypeElement): Boolean {
    return types.isAssignable(element.asType(), elements.mirrorOf(Fqdn.ACTIVITY))
}

/**
 * @return {@code true} if [element] is derived from Android `Fragment`
 */
fun ProcessingEnvironment.isDerivedFromFragment(element: TypeElement): Boolean {
    val type = element.asType()
    return types.isAssignable(type, elements.mirrorOf(Fqdn.FRAGMENT)) || run {
        val fragmentElement = elements.getTypeElement(Fqdn.SUPPORT_FRAGMENT)
        if (fragmentElement == null) {
            false
        } else {
            types.isAssignable(type, fragmentElement.asType())
        }
    }
}
