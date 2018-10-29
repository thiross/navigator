package com.tutuur.navigator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Intent} builder helper class. to create bundles.
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class IntentBuilder {

    /**
     * The target bundle.
     */
    protected final Bundle bundle;

    public IntentBuilder() {
        // initialize target bundle.
        bundle = new Bundle();
    }

    public IntentBuilder(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Put byte extra to bundle.
     *
     * @param name  extra name.
     * @param value byte value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, byte value) {
        if (value != 0) {
            bundle.putByte(name, value);
        }
        return this;
    }

    /**
     * Put char extra to bundle.
     *
     * @param name  extra name.
     * @param value char value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, char value) {
        if (value != 0) {
            bundle.putChar(name, value);
        }
        return this;
    }

    /**
     * Put short extra to bundle.
     *
     * @param name  extra name.
     * @param value short value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, short value) {
        if (value != 0) {
            bundle.putShort(name, value);
        }
        return this;
    }

    /**
     * Put int extra to bundle.
     *
     * @param name  extra name.
     * @param value int value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, int value) {
        if (value != 0) {
            bundle.putInt(name, value);
        }
        return this;
    }

    /**
     * Put float extra to bundle.
     *
     * @param name  extra name.
     * @param value float value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, float value) {
        if (value != 0.0f) {
            bundle.putFloat(name, value);
        }
        return this;
    }

    /**
     * Put double extra to bundle.
     *
     * @param name  extra name.
     * @param value double value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, double value) {
        if (value != 0.0) {
            bundle.putDouble(name, value);
        }
        return this;
    }

    /**
     * Put String extra to bundle.
     *
     * @param name  extra name.
     * @param value string value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, String value) {
        if (value != null) {
            bundle.putString(name, value);
        }
        return this;
    }

    /**
     * Put {@code Serializable} extra to bundle.
     *
     * @param name  extra name.
     * @param value serializable value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, Serializable value) {
        if (value != null) {
            bundle.putSerializable(name, value);
        }
        return this;
    }

    /**
     * Put {@code Parcelable} extra to bundle.
     *
     * @param name  extra name.
     * @param value parcelable value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, Parcelable value) {
        if (value != null) {
            bundle.putParcelable(name, value);
        }
        return this;
    }

    /**
     * Put {@code byte[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code byte[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, byte[] value) {
        if (value != null) {
            bundle.putByteArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code char[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code char[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, char[] value) {
        if (value != null) {
            bundle.putCharArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code short[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code short[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, short[] value) {
        if (value != null) {
            bundle.putShortArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code int[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code int[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, int[] value) {
        if (value != null) {
            bundle.putIntArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code long[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code long[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, long[] value) {
        if (value != null) {
            bundle.putLongArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code float[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code float[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, float[] value) {
        if (value != null) {
            bundle.putFloatArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code double[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code double[]} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, double[] value) {
        if (value != null) {
            bundle.putDoubleArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code String[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code String} array value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, String[] value) {
        if (value != null) {
            bundle.putStringArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code Parcelable[]} extra to bundle.
     *
     * @param name  extra name.
     * @param value {@code Parcelable} array value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder put(String name, Parcelable[] value) {
        if (value != null) {
            bundle.putParcelableArray(name, value);
        }
        return this;
    }

    /**
     * Put {@code List<String>} value to bundle.
     *
     * @param name  extra name.
     * @param value {@code List<String>} value.
     * @return {@code this} to chain calls.
     */
    public IntentBuilder putStringList(String name, List<String> value) {
        if (value != null) {
            ArrayList<String> list;
            if (value instanceof ArrayList) {
                list = (ArrayList<String>) value;
            } else {
                list = new ArrayList<>(value);
            }
            bundle.putStringArrayList(name, list);
        }
        return this;
    }

    /**
     * Put {@code List<? extends Parcelable>} value to bundle.
     *
     * @param name  extra name.
     * @param value {@code List<? extends Parcelable>} value.
     * @return {@code this} to chain calls.
     */
    @SuppressWarnings("unchecked")
    public IntentBuilder putParcelableList(String name, List<? extends Parcelable> value) {
        if (value != null) {
            ArrayList<? extends Parcelable> list;
            if (value instanceof ArrayList) {
                list = (ArrayList<? extends Parcelable>) value;
            } else {
                list = new ArrayList<>(value);
            }
            bundle.putParcelableArrayList(name, list);
        }
        return this;
    }

    /**
     * Parse {@code path} and set fields from {@code path} segments.
     *
     * @param path part of scheme uri.
     * @return null if parsing failed.
     */
    public IntentBuilder parse(String path) {
        return null;
    }

    /**
     * Bind extras to target fields.
     *
     * @param object the target, should be activity or fragment.
     */
    public void bind(Object object) {
    }

    /**
     * Create {@link Intent} from _IntentBuilder attributes.
     *
     * @param context need by {@link Intent} constructor.
     * @return {@link Intent} related to navigation target.
     */
    public Intent build(Context context) {
        throw new RuntimeException("IntentBuilder doesn't implement build method.");
    }

    /**
     * Start a target intent build by this builder.
     *
     * @param context the caller context.
     */
    public void startActivity(Context context) {
        throw new RuntimeException("IntentBuilder doesn't implement startActivity method.");
    }

    /**
     * Start a target intent build by this builder for result.
     *
     * @param activity    the caller activity.
     * @param requestCode to identify request.
     */
    public void startActivityForResult(Activity activity, int requestCode) {
        throw new RuntimeException("IntentBuilder doesn't implement startActivityForResult method.");
    }

    /**
     * Start a target intent build by this builder for result.
     *
     * @param fragment    the caller fragment.
     * @param requestCode to identify request.
     */
    public void startActivityForResult(Fragment fragment, int requestCode) {
        throw new RuntimeException("IntentBuilder doesn't implement startActivityForResult method.");
    }

    /**
     * Start a target intent build by this builder for result.
     *
     * @param fragment    the caller fragment.
     * @param requestCode to identify request.
     */
    public void startActivityForResult(android.support.v4.app.Fragment fragment, int requestCode) {
        throw new RuntimeException("IntentBuilder doesn't implement startActivityForResult method.");
    }
}
