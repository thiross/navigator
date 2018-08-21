package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigatordemo.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BundleExtra
    boolean available = true;

    @BundleExtra
    byte b;

    @BundleExtra
    char c;

    @BundleExtra
    int age;

    @BundleExtra
    long timestamp;

    @BundleExtra
    float threshold;

    @BundleExtra
    double ratio;

    @BundleExtra("uid")
    String id;

    @BundleExtra
    List<String> tags;

    /*
    static class Inner {
        @BundleExtra
        String id;
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigateTo(View view) {
        Navigator.navigateToSecondActivity()
                .username("David")
                .startActivity(this);
    }
}
