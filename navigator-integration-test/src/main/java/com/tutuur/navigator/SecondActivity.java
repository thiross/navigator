package com.tutuur.navigator;

import android.app.Activity;
import android.os.Bundle;

import com.tutuur.navigator.model.Cat;

import java.util.List;

@Navigation
public class SecondActivity extends Activity {

    @BundleExtra
    byte byteValue;

    @BundleExtra
    char charValue;

    @BundleExtra
    short shortValue;

    @BundleExtra
    int intValue;

    @BundleExtra
    float floatValue;

    @BundleExtra
    double doubleValue;

    @BundleExtra
    String stringValue;

    @BundleExtra
    Cat cat;

    @BundleExtra
    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
    }
}
