package com.tutuur.navigator;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

    @BundleExtra
    int baseValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
