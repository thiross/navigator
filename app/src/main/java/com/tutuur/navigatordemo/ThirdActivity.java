package com.tutuur.navigatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation(schemes = {"example://third/:name", "http://www.example.com/third"})
public class ThirdActivity extends AppCompatActivity {

    @BundleExtra
    String name;

    @BundleExtra
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }
}
