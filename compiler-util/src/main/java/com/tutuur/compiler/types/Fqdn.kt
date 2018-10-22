package com.tutuur.compiler.types

internal object Fqdn {
    /**
     * [String] Fqdn.
     */
    const val STRING = "java.lang.String"

    /**
     * [Serializable] Fqdn.
     */
    const val SERIALIZABLE = "java.io.Serializable"

    /**
     * [List] Fqdn.
     */
    const val LIST = "java.util.List"

    /**
     * Android `Parcelabel` Fqdn.
     */
    const val PARCELABLE = "android.os.Parcelable"

    /**
     * Android `Activity` Fqdn.
     */
    const val ACTIVITY = "android.app.Activity"

    /**
     * Android `Fragment` Fqdn.
     */
    const val FRAGMENT = "android.app.Fragment"

    /**
     * Android support `Fragment` Fqdn.
     */
    const val SUPPORT_FRAGMENT = "android.support.v4.app.Fragment"
}