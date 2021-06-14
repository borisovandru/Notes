package com.android.notes.domain;

import androidx.annotation.StringRes;

public class Notes {

    @StringRes
    private final int name ;

    @StringRes
    private final int description;

    @StringRes
    private final int date;

    public Notes(int name, int description, int date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public int getName() {
        return name;
    }

    public int getDescription() {
        return description;
    }

    public int getDate() {
        return date;
    }
}
