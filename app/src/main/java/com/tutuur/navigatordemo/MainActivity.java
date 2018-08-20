package com.tutuur.navigatordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

import java.util.Date;

@Navigation(
        schemes = {
                "scheme://main/:id",
                "test://main",
                "https://www.tutuur.com/main/:id"
        }
)
public class MainActivity extends AppCompatActivity {

    @BundleExtra
    boolean available = true;

    @BundleExtra
    byte b;

    @BundleExtra
    char c;

    @BundleExtra
    int age;

    @BundleExtra
    long timestamp;

    @BundleExtra
    float threshold;

    @BundleExtra
    double ratio;

    @BundleExtra("uid")
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

    public void navigateTo(View view) {
        final Data data = new Data();
        data.text = "N/A";
        Navigator.navigateToSecondActivity()
                .email("fake@fake.fake")
                .username("zale")
                .timestamp(new Date().getTime())
                .data(data)
                .startActivity(this);
    }
}
