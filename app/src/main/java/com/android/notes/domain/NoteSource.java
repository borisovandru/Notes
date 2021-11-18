package com.android.notes.domain;

public interface NoteSource {
    NoteSource init(NotesSourceResponse notesSourceResponse);

    Note getNoteData(int position);

    int size();

    void deleteNoteData(int position);

    void updateNoteData(int position, Note noteData);

    void addNoteData(Note noteData);

    void clearNoteData();
}
