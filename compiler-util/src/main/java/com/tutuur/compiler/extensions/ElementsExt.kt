package com.tutuur.compiler.extensions

import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

/**
 * @return package name of [element]
 */
fun Elements.packageNameOf(element: TypeElement): String {
    return getPackageOf(element).qualifiedName.toString()
}

/**
 * @return [TypeElement] of [fqdn]
 */
fun Elements.elementOf(fqdn: String): TypeElement = getTypeElement(fqdn)

/**
 * @return [TypeMirror] of [fqdn]
 */
fun Elements.mirrorOf(fqdn: String): TypeMirror = elementOf(fqdn).asType()