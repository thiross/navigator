package com.tutuur.navigator.models

import com.tutuur.navigator.BundleExtra
import javax.lang.model.element.VariableElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

data class Field(val element: VariableElement, val annotation: BundleExtra) {
    /**
     * [TypeMirror] of [element].
     */
    val type: TypeMirror = element.asType()

    /**
     * Component name for `get` method in `BundleBuilder`.
     * For example `Byte` in {@code getByte} method.
     */
    val component = run {
        val name = if (type.kind == TypeKind.ARRAY) {
            (type as ArrayType).componentType
        } else {
            type
        }.toString()
        val index = name.lastIndexOf('.')
        if (index >= 0) {
            name.substring(index + 1)
        } else {
            name
        }.capitalize()
    }

    /**
     * Variable name of [element].
     */
    val name = element.simpleName.toString()

    /**
     * Whether [type] is primitive.
     */
    val isPrimitive = type.kind.isPrimitive
}
