package com.tutuur.navigator;

import android.os.Bundle;

import com.tutuur.navigator.model.Cat;

import java.util.List;

@Navigation
public class SecondActivity extends CommonActivity {

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
    Cat[] cats;

    @BundleExtra
    List<String> stringList;

    @SuppressWarnings("unused")
    int ignore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
    }
}
