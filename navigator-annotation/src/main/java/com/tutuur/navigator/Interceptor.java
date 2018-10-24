package com.tutuur.navigator;

import android.content.Context;
import android.content.Intent;

@SuppressWarnings("unused")
public interface Interceptor {

    /**
     * Pre interceptor method before {@link Intent} building.
     *
     * @param context the caller context.
     * @return {@code true} to block navigating.
     */
    boolean preIntercept(Context context);

    /**
     * Interceptor method after {@link Intent} is created.
     *
     * @param intent the called intent.
     * @return {@code true} to block navigating.
     */
    boolean intercept(Intent intent);
}
