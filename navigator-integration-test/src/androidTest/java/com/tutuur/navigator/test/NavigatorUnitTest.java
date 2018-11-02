package com.tutuur.navigator.test;

import com.tutuur.navigator.Navigator;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NavigatorUnitTest {

    @Test
    public void navigatorRoute_shouldContainAllAnnotatedPages() {
        assertThat(Navigator.parse("/common"))
                .isNotNull();
        assertThat(Navigator.parse("/second/123"))
                .isNotNull();
        assertThat(Navigator.parse("/unknown"))
                .isNull();
    }
}

