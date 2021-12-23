package com.android.notes.domain;

import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String title;
    private String note;
    private long dateCreated;
    private int color;

    public Note(String title, String note, long dateCreated, int color) {
        this.title = title;
        this.note = note;
        this.dateCreated = dateCreated;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
