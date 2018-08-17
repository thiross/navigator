package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation
public class SecondActivity extends AppCompatActivity {

    @BundleExtra
    String username;

    @BundleExtra
    String email;

    @BundleExtra
    long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
