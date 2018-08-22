package com.tutuur.navigatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation(schemes = {"" +
        "example://third1/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "example://third1/:name",
        "example://third1/:name",
        "example://third1/:name",
        "example://third1/:name",
        "example://third1/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "example://third2/:name",
        "example://third3/:name",
        "example://third4/:name",
        "example://third5/:name",
        "example://third6/:name",
        "example://third7/:name",
        "example://third8/:name",
        "example://third9/:name",
        "example://third10/:name",
        "example://third11/:name",
        "http://www.example.com/third"
})
public class ThirdActivity extends AppCompatActivity {

    @BundleExtra
    String name;

    @BundleExtra
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_third);
        TextView text = findViewById(R.id.text_view);
        text.setText(String.format("name: %s, query: %s", name, query));
    }
}
