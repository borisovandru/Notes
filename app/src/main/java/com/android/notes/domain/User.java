package com.android.notes.domain;

public class User {

    public static String nameUser;
    private static final User userData = new User();

    public User() {

    }

    public static void getUserData(String name) {
        nameUser = name;
    }

}