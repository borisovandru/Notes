package com.android.notes.model;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String title;
    private String note;
    private long dateCreated;
    private String color;

    public Note(String title, String note, long dateCreated, String color) {
        this.title = title;
        this.note = note;
        this.dateCreated = dateCreated;
        this.color = color;
    }

    public Note() {
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean compare(Note otherNote) {
        return Math.abs(this.getDateCreated() - otherNote.getDateCreated()) < 0.0001f;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
