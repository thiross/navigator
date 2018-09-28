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
 * print info [message] to output.
 */
fun ProcessingEnvironment.i(message: String) {
    messager.printMessage(Diagnostic.Kind.NOTE, message)
}

/**
 * print warning [message] to output.
 */
fun ProcessingEnvironment.w(message: String) {
    messager.printMessage(Diagnostic.Kind.WARNING, message)
}

/**
 * print error [message] to output, and abort execution.
 */
fun ProcessingEnvironment.e(message: String) {
    messager.printMessage(Diagnostic.Kind.ERROR, message)
    System.exit(1)
}

/**
 * @return [true] if [element] is [String]
 */
fun ProcessingEnvironment.isString(element: TypeElement): Boolean {
    return types.isSameType(element.asType(), elements.mirrorOf(Fqdn.STRING))
}

/**
 * @return [true] if [element] is Android `Activity`
 */
fun ProcessingEnvironment.isActivity(element: TypeElement): Boolean {
    return types.isAssignable(element.asType(), elements.mirrorOf(Fqdn.ACTIVITY))
}

/**
 * @return [true] if [element] is Android `Fragment`
 */
fun ProcessingEnvironment.isFragment(element: TypeElement): Boolean {
    val type = element.asType()
    return types.isAssignable(type, elements.mirrorOf(Fqdn.FRAGMENT)) ||
            types.isAssignable(type, elements.mirrorOf(Fqdn.SUPPORT_FRAGMENT))
}