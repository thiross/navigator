package com.tutuur.compiler.extensions

import javax.lang.model.element.VariableElement

/**
 * @return qualified class name of [VariableElement].
 */
val VariableElement.qualifiedClassName: String
    get() = this.asType().toString()

/**
 * @return class name (not full qualified) of [VariableElement].
 */
val VariableElement.className: String
    get() {
        val name = qualifiedClassName
        val index = name.lastIndexOf('.')
        return if (index >= 0) {
            name.substring(index + 1)
        } else {
            name
        }
    }