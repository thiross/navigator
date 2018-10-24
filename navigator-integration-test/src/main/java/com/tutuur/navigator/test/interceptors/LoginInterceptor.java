package com.tutuur.navigator.test.interceptors;

import android.content.Context;
import android.content.Intent;

import com.tutuur.navigator.Interceptor;

public class LoginInterceptor implements Interceptor {

    @Override
    public boolean preIntercept(Context context) {
        return false;
    }

    @Override
    public boolean intercept(Intent intent) {
        return false;
    }
}
