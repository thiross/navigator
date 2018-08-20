package com.tutuur.navigator;

import android.content.Intent;

public interface Interceptor {
    boolean intercept(Intent intent);
}
