package com.tutuur.navigator;

import android.test.ActivityInstrumentationTestCase2;

final public class SimpleTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public SimpleTest() {
        super(MainActivity.class);
    }

    public void testActivityStarts() {
        getActivity();
        getInstrumentation().waitForIdleSync();
    }
}
