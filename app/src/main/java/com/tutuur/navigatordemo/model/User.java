package com.tutuur.navigatordemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    private User(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
