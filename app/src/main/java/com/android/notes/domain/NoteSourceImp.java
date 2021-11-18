package com.android.notes.domain;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteSourceImp implements NoteSource {

    private List<Object> objectListItem;
    private final Resources resources;

    public NoteSourceImp(Resources resources) {
        this.resources = resources;
        this.objectListItem = new ArrayList<>();
    }

    @Override
    public NoteSource init(NotesSourceResponse notesSourceResponse) {
        if (objectListItem.isEmpty()) {
            objectListItem = new ArrayList<>();
            // строки заголовков из ресурсов
            String[] titles = resources.getStringArray(com.android.notes.R.array.notes);
            // строки описаний из ресурсов
            String[] descriptions = resources.getStringArray(com.android.notes.R.array.descriptions);
            // изображения
            // заполнение источника данных
            for (int i = 0; i < descriptions.length; i++) {
                Note note = new Note(titles[i], descriptions[i], Calendar.getInstance().getTime());
                objectListItem.add(note.getDate());
                objectListItem.add(note);
            }
        }
        return this;
    }

    public boolean isGroupItem(int position) {
        return !(objectListItem.get(position) instanceof Note);
    }

    @android.annotation.SuppressLint("SimpleDateFormat")
    public String getGroupTitle(int position) {
        Date date = (Date) objectListItem.get(position);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public Note getNoteData(int position) {
        return (Note) objectListItem.get(position);
    }

    public int size() {
        return objectListItem.size();
    }

    @Override
    public void deleteNoteData(int position) {
        objectListItem.remove(position);
    }

    @Override
    public void updateNoteData(int position, Note noteData) {
        objectListItem.set(position, noteData);
    }

    @Override
    public void addNoteData(Note noteData) {
        objectListItem.add(noteData);
    }

    @Override
    public void clearNoteData() {
        objectListItem.clear();
    }
}
