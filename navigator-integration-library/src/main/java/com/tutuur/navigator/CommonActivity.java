package com.tutuur.navigator;

import android.app.Activity;
import android.os.Bundle;

public class CommonActivity extends BaseActivity {

    @BundleExtra
    int commonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
