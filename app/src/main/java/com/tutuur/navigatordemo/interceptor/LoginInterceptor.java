package com.tutuur.navigatordemo.interceptor;

import android.content.Context;
import android.content.Intent;

import com.tutuur.navigator.Interceptor;
import com.tutuur.navigatordemo.Navigator;

public class LoginInterceptor implements Interceptor {

    @Override
    public boolean intercept(Context context) {
        if (Math.random() > 0.5) {
            return false;
        }
        Navigator.navigateToLoginActivity()
                .startActivity(context);
        return true;
    }

    @Override
    public boolean intercept(Intent intent) {
        return false;
    }
}
