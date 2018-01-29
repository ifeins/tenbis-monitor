package com.ifeins.tenbis.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * @author ifeins
 */

public class User {

    private static transient User sCurrentUser;

    @Nullable
    public static User getCurrentUser() {
        return sCurrentUser;
    }

    public static void setCurrentUser(@Nullable User currentUser) {
        sCurrentUser = currentUser;
    }

    @SerializedName("userId")
    private String mUserId;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("password")
    private String mPassword;

    public User(String userId, String email, String password) {
        mUserId = userId;
        mEmail = email;
        mPassword = password;
    }


}
