package com.android.notes.model;

import java.util.ArrayList;
import java.util.List;

public class NotesRepository {
    private List<Note> notes;

    public NotesRepository() {
        notes = new ArrayList<>();
    }

    public boolean add(Note note) {
        note.setId(notes.size());
        return notes.add(note);
    }

    public boolean remove(Note note) {
        return notes.remove(note);
    }

    public void remove(int index) {
        notes.remove(index);
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public Note get(int index) {
        return notes.get(index);
    }

    public Note findByID(int id) {
        for (Note note : notes) {
            if (note.getId() == id) {
                return note;
            }
        }
        return null;
    }
}
