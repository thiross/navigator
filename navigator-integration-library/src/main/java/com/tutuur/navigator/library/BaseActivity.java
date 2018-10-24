package com.tutuur.navigator.library;

import android.app.Activity;
import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;

public class BaseActivity extends Activity {

    @BundleExtra
    int baseValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
