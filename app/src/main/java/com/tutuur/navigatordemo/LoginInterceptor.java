package com.tutuur.navigatordemo;

import android.content.Intent;

import com.tutuur.navigator.Interceptor;

public class LoginInterceptor implements Interceptor {

    @Override
    public boolean intercept(Intent intent) {
        return Math.random() > 0.5;
    }
}
