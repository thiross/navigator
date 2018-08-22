package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;
import com.tutuur.navigatordemo.interceptor.LoginInterceptor;
import com.tutuur.navigatordemo.model.User;

import java.util.List;

@Navigation(interceptors = LoginInterceptor.class)
public class SecondActivity extends AppCompatActivity {

    @BundleExtra
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_second);
    }
}
