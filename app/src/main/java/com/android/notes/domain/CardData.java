package com.android.notes.domain;

public class CardData {
    private final String name;
    private final int picture;
    private final String description;

    public CardData(String name, int picture, String description) {
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public int getPicture() {
        return picture;
    }

    public String getDescription() {
        return description;
    }
}