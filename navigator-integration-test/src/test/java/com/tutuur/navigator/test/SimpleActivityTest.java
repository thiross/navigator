package com.tutuur.navigator.test;

import android.content.Intent;

import com.google.common.collect.Lists;
import com.tutuur.navigator.test.model.Cat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class SimpleActivityTest {

    @Test
    public void verifyIntentExtra() {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .get();

        final char charValue = 'c';
        final byte byteValue = 120;
        final short shortValue = 254;
        final int intValue = -20;
        final float floatValue = -2.53f;
        final double doubleValue = 3656.0;
        final String stringValue = "David";
        final Cat cat = new Cat("Lucky", 1);

        final List<String> stringList = Lists.newArrayList(
                "A", "B", "D", "C"
        );

        Navigator.navigateToSecondActivity()
                .charValue(charValue)
                .byteValue(byteValue)
                .shortValue(shortValue)
                .intValue(intValue)
                .floatValue(floatValue)
                .doubleValue(doubleValue)
                .stringValue(stringValue)
                .cat(cat)
                .stringList(stringList)
                .startActivity(mainActivity);

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        final Intent intent = application.getNextStartedActivity();
        assertThat(intent)
                .isNotNull();

        SecondActivity secondActivity = Robolectric.buildActivity(SecondActivity.class, intent)
                .create()
                .get();
        assertThat(secondActivity.charValue)
                .isEqualTo(charValue);
        assertThat(secondActivity.byteValue)
                .isEqualTo(byteValue);
        assertThat(secondActivity.shortValue)
                .isEqualTo(shortValue);
        assertThat(secondActivity.intValue)
                .isEqualTo(intValue);
        assertThat(secondActivity.floatValue)
                .isEqualTo(floatValue);
        assertThat(secondActivity.doubleValue)
                .isEqualTo(doubleValue);
        assertThat(secondActivity.stringValue)
                .isEqualTo(stringValue);
        assertThat(secondActivity.cat)
                .isEqualTo(cat);
        assertThat(secondActivity.stringList)
                .isEqualTo(stringList);
    }
}