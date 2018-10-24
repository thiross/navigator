package com.tutuur.navigator.library;

import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;

public class CommonActivity extends BaseActivity {

    @BundleExtra
    int commonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
