package com.tutuur.navigatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tutuur.navigator.Navigation;

@Navigation
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }
}
