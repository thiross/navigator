package com.tutuur.navigator;

import android.content.Context;
import android.content.Intent;

public interface Interceptor {

    boolean intercept(Context context);

    boolean intercept(Intent intent);
}
