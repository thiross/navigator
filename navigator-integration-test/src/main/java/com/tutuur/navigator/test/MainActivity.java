package com.tutuur.navigator.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tutuur.navigator.Navigator;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigateTo(View view) {
        Navigator.parse("/common")
                .startActivity(this);
    }
}
