package com.tutuur.navigatordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tutuur.navigator.BundleExtra;
import com.tutuur.navigator.Navigation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @BundleExtra
    List<String> tags;

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
        final User user = new User();
        user.name = "David";

        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user);
        users.add(user);
        users.add(user);
        Navigator.navigateToSecondActivity()
                .email("fake@fake.fake")
                .username("zale")
                .timestamp(new Date().getTime())
                .user(user)
                .tags(list)
                .list(users)
                .startActivity(this);
    }
}
