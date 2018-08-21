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

    @BundleExtra
    String email;

    @BundleExtra
    long timestamp;

    @BundleExtra
    User user;

    @BundleExtra
    String[] array;

    @BundleExtra
    User[] userArray;

    @BundleExtra
    List<String> tags;

    @BundleExtra
    List<User> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Navigator.bind(this);
        setContentView(R.layout.activity_second);
        TextView tv = findViewById(R.id.text_view);
        tv.setText(String.format("%s %s %s %s %s",
                username,
                user == null ? "null" : user.name,
                (tags != null && tags.size() > 2) ? tags.get(1) : "null",
                (userArray != null && userArray.length > 5) ? userArray[4].name : "array",
                (list != null && list.size() > 2) ? list.get(1).name : "N/A"));
    }

    public void navigateTo(View view) {
        Navigator.navigateTo(this, "scheme://thirD");
    }
}
