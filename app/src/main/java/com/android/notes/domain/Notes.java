package com.android.notes.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes implements Parcelable {
    private final String noteName;
    private final String noteDescription;
    private final String noteDate;
    private final String note;

    public Notes(String noteName, String noteDescription, String noteDate, String note) {
        this.noteName = noteName;
        this.noteDescription = noteDescription;
        this.noteDate = noteDate;
        this.note = note;
    }

    protected Notes(Parcel in) {
        noteName = in.readString();
        noteDescription = in.readString();
        noteDate = in.readString();
        note = in.readString();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getNoteName());
        parcel.writeString(getNoteDescription());
        parcel.writeString(getNoteDate());
        parcel.writeString(getNote());
    }

    public String getNoteName() {
        return noteName;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public String getNote() {
        return note;
    }

}