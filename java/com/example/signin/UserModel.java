package com.example.signin;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    static String feedback;
    static String username;
    static String  email;
    static String rating;

    public UserModel( String username, String email,String feedback) {
        this.feedback = feedback;
        this.username=username;
        this.email=email;
        this.rating="5";
    }

    public UserModel() {

    }

    protected UserModel(Parcel in) {
        feedback = in.readString();
        username = in.readString();
        email = in.readString();
        rating = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(feedback);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(rating);
    }
}
