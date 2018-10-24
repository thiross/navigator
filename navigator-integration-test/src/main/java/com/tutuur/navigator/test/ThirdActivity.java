package com.tutuur.navigator.test;

import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.library.BaseActivity;

public class ThirdActivity extends BaseActivity {

    @BundleExtra
    int third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
