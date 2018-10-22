package com.tutuur.compiler.extensions

import javax.lang.model.element.VariableElement
import javax.lang.model.type.ArrayType

/**
 * @return class name (not full qualified) of [VariableElement]. if [VariableElement] is a array,
 * return element class name.
 */
val VariableElement.rawClassName: String
    get() {
        val type = asType()
        val rawType = if (type is ArrayType) {
            type.componentType
        } else {
            type
        }
        val name = rawType.toString()
        val index = name.lastIndexOf('.')
        return if (index >= 0) {
            name.substring(index + 1)
        } else {
            name
        }
    }