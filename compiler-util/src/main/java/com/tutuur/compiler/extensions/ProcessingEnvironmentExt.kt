package com.tutuur.compiler.extensions

import com.tutuur.compiler.types.Fqdn
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
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
 * @return {@code true} if [type] is [String]
 */
fun ProcessingEnvironment.isString(type: TypeMirror): Boolean {
    return types.isSameType(type, elements.mirrorOf(Fqdn.STRING))
}

/**
 * @return {@code true} if [type] is exactly Activity class.
 */
fun ProcessingEnvironment.isActivity(type: TypeMirror): Boolean {
    return types.isSameType(type, elements.mirrorOf(Fqdn.ACTIVITY))
}

/**
 * @return {@code true} if [type] is exactly Fragment class.
 */
fun ProcessingEnvironment.isFragment(type: TypeMirror): Boolean {
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
 * @return {@code true} if [type] derives from [Serializable]
 */
fun ProcessingEnvironment.isDerivedFromSerializable(type: TypeMirror): Boolean {
    return types.isAssignable(type, elements.mirrorOf(Fqdn.SERIALIZABLE))
}

/**
 * @return {@code true} if [type] derives from `Parcelable`
 */
fun ProcessingEnvironment.isDerivedFromParcelable(type: TypeMirror): Boolean {
    return types.isAssignable(type, elements.mirrorOf(Fqdn.PARCELABLE))
}

/**
 * @return {@code true} if [type] derives from Android `Activity`
 */
fun ProcessingEnvironment.isDerivedFromActivity(type: TypeMirror): Boolean {
    return types.isAssignable(type, elements.mirrorOf(Fqdn.ACTIVITY))
}

/**
 * @return {@code true} if [type] derives from Android `Fragment`
 */
fun ProcessingEnvironment.isDerivedFromFragment(type: TypeMirror): Boolean {
    return types.isAssignable(type, elements.mirrorOf(Fqdn.FRAGMENT)) || run {
        val fragmentElement = elements.getTypeElement(Fqdn.SUPPORT_FRAGMENT)
        if (fragmentElement == null) {
            false
        } else {
            types.isAssignable(type, fragmentElement.asType())
        }
    }
}
