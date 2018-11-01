package com.tutuur.navigator.library;

import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation(page = "common")
public class CommonActivity extends BaseActivity {

    @BundleExtra
    int commonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
