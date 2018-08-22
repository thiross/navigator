package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.widget.TextView;

import com.tutuur.navigator.BundleExtra;

public class DerivedActivity extends BaseActivity {

    @BundleExtra
    String valueInDerived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_derived);

        TextView textBase = findViewById(R.id.text_base);
        textBase.setText(valueInBase);

        TextView text = findViewById(R.id.text_derived);
        text.setText(valueInDerived);
    }
}
