package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

@Navigation
public class SecondActivity extends AppCompatActivity {

    @BundleExtra
    String username;

    @BundleExtra
    String email;

    @BundleExtra
    long timestamp;

    @BundleExtra
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_second);
        TextView tv = findViewById(R.id.text_view);
        tv.setText(String.format("%s%s", username, data == null ? "null" : data.text));
    }

    public void navigateTo(View view) {
        Navigator.navigateTo(this, "scheme://thirD");
    }
}
