package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation(
        schemes = {
                "scheme://main/:id",
                "https://www.tutuur.com/main/:id"
        }
)
public class MainActivity extends AppCompatActivity {

    @BundleExtra
    String id;

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
}
