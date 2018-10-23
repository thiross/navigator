package com.tutuur.compiler.extensions

import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

/**
 * @return [TypeElement] of [fqdn]
 */
internal fun Elements.elementOf(fqdn: String): TypeElement = getTypeElement(fqdn)

/**
 * @return [TypeMirror] of [fqdn]
 */
internal fun Elements.mirrorOf(fqdn: String): TypeMirror = elementOf(fqdn).asType()
