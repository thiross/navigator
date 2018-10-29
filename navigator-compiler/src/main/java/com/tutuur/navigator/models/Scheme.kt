package com.tutuur.navigator.models

/**
 * Scheme object of navigation.
 * Scheme example:
 *   scheme://page[/subpage]
 */
data class Scheme(val page: String, val subPage: String) {

    fun isEmpty() = page.isEmpty() && subPage.isEmpty()

    companion object {
        val EMPTY = Scheme("", "")
    }
}
