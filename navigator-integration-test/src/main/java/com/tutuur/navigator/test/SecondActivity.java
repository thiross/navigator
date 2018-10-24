package com.tutuur.navigator.test;

import android.os.Bundle;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;
import com.tutuur.navigator.library.CommonActivity;
import com.tutuur.navigator.test.model.Cat;

import java.util.List;

@Navigation(schemes = {
        "example://second/:id",
        "http://www.example.com/second/:id"
})
public class SecondActivity extends CommonActivity {

    @BundleExtra(key = "flag")
    boolean boolValue;

    @BundleExtra
    byte byteValue;

    @BundleExtra
    char charValue;

    @BundleExtra
    short shortValue;

    @BundleExtra(autowired = true)
    int intValue;

    @BundleExtra(key = "float")
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
    List<Cat> catList;

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