package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tutuur.navigator.BundleExtra;

public class BaseActivity extends AppCompatActivity {

    @BundleExtra
    String valueInBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_base);
        TextView text = findViewById(R.id.text_base);
        text.setText(valueInBase);
    }
}
