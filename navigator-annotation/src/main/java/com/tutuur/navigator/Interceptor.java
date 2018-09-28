package com.tutuur.navigator;

import android.content.Context;
import android.content.Intent;

@SuppressWarnings("unused")
public interface Interceptor {

    boolean intercept(Context context);

    boolean intercept(Intent intent);
}
