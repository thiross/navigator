package com.tutuur.navigator.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class Cat implements Parcelable {

    private String name;

    private int gender;

    public Cat(String name, int gender) {
        this.name = name;
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.gender);
    }

    private Cat(Parcel in) {
        this.name = in.readString();
        this.gender = in.readInt();
    }

    public static final Parcelable.Creator<Cat> CREATOR = new Parcelable.Creator<Cat>() {
        @Override
        public Cat createFromParcel(Parcel source) {
            return new Cat(source);
        }

        @Override
        public Cat[] newArray(int size) {
            return new Cat[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cat cat = (Cat) o;
        return gender == cat.gender &&
                Objects.equal(name, cat.name);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Name: %s, Gender: %d", name, gender);
    }
}
