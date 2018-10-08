package com.tutuur.navigator;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Bundle builder helper class. to create bundles.
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class BundleBuilder {

    /**
     * The target bundle.
     */
    protected Bundle bundle;

    public BundleBuilder() {
        // initialize target bundle.
        bundle = new Bundle();
    }

    /**
     * Put byte {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value byte value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, byte value) {
        if (value != 0) {
            bundle.putByte(name, value);
        }
        return this;
    }

    /**
     * Put char {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value char value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, char value) {
        if (value != 0) {
            bundle.putChar(name, value);
        }
        return this;
    }

    /**
     * Put short {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value short value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, short value) {
        if (value != 0) {
            bundle.putShort(name, value);
        }
        return this;
    }

    /**
     * Put int {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value int value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, int value) {
        if (value != 0) {
            bundle.putInt(name, value);
        }
        return this;
    }

    /**
     * Put float {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value float value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, float value) {
        if (value != 0.0f) {
            bundle.putFloat(name, value);
        }
        return this;
    }

    /**
     * Put double {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value double value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, double value) {
        if (value != 0.0) {
            bundle.putDouble(name, value);
        }
        return this;
    }

    /**
     * Put String {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value string value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, String value) {
        if (value != null) {
            bundle.putString(name, value);
        }
        return this;
    }

    /**
     * Put {@code Serializable} {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value serializable value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, Serializable value) {
        if (value != null) {
            bundle.putSerializable(name, value);
        }
        return this;
    }

    /**
     * Put {@code Parcelable} {@code value} extra to bundle.
     *
     * @param name  extra name.
     * @param value parcelable value.
     * @return {@code this} to chain calls.
     */
    public BundleBuilder put(String name, Parcelable value) {
        if (value != null) {
            bundle.putParcelable(name, value);
        }
        return this;
    }
}
