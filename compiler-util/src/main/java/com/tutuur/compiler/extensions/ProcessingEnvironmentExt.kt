package com.tutuur.compiler.extensions

import com.tutuur.compiler.types.Fqdn
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
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

val ProcessingEnvironment.context: TypeMirror
    get() = elements.mirrorOf(Fqdn.CONTEXT)

/**
 * @return activity element from Fqdn.
 */
val ProcessingEnvironment.activity: TypeMirror
    get() = elements.mirrorOf(Fqdn.ACTIVITY)

/**
 * @return fragment element from Fqdn.
 */
val ProcessingEnvironment.fragment: TypeMirror
    get() = elements.mirrorOf(Fqdn.FRAGMENT)

/**
 * create support fragment element from Fqdn.
 * @return {@code null} if support library is not a dependency.
 */
val ProcessingEnvironment.supportFragment: TypeMirror?
    get() = elements.getTypeElement(Fqdn.SUPPORT_FRAGMENT)?.asType()

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

/**
 * @return {@code true} if [type] is [Array] of [componentType].
 */
fun ProcessingEnvironment.isArrayOf(type: TypeMirror, componentType: TypeMirror): Boolean {
    return types.isAssignable(type, types.getArrayType(componentType))
}

/**
 * @return {@code true} if [type] is [Array] of primitives.
 */
fun ProcessingEnvironment.isPrimitiveArray(type: TypeMirror): Boolean {
    if (type.kind != TypeKind.ARRAY) {
        return false
    }
    return (type as ArrayType).componentType.kind.isPrimitive
}

/**
 * @return {@code true} if [type] is [Array] of [String]
 */
fun ProcessingEnvironment.isStringArray(type: TypeMirror): Boolean {
    return isArrayOf(type, elements.mirrorOf(Fqdn.STRING))
}

/**
 * @return {@code true} if [type] is [Array] of `Parcelable`
 */
fun ProcessingEnvironment.isParcelableArray(type: TypeMirror): Boolean {
    return isArrayOf(type, elements.mirrorOf(Fqdn.PARCELABLE))
}

/**
 * @return {@code true} if [type] is [genericType]<[componentType]>.
 */
private fun ProcessingEnvironment.isParameterizedType(type: TypeMirror,
                                                      genericType: TypeMirror,
                                                      componentType: TypeMirror): Boolean {
    if (type !is DeclaredType) {
        return false
    }
    if (!types.isAssignable(type, types.erasure(genericType))) {
        return false
    }
    val parameterTypes = type.typeArguments
    if (parameterTypes.size != 1) {
        return false
    }
    return types.isAssignable(parameterTypes[0], componentType)
}

/**
 * @return {@code true} if [type] is [List] of [String]
 */
fun ProcessingEnvironment.isStringList(type: TypeMirror): Boolean {
    return isParameterizedType(type, elements.mirrorOf(Fqdn.LIST), elements.mirrorOf(Fqdn.STRING))
}

/**
 * @return {@code true} if [type] is [List] of `Parcelable`
 */
fun ProcessingEnvironment.isParcelableList(type: TypeMirror): Boolean {
    return isParameterizedType(type,
            elements.mirrorOf(Fqdn.LIST),
            elements.mirrorOf(Fqdn.PARCELABLE))
}