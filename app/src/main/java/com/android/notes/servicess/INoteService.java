package com.android.notes.servicess;

import com.android.notes.model.Note;
import com.android.notes.model.NotesRepository;

public interface INoteService {
    //Получение заметок
    NotesRepository getNotes();

    //Генерация заметок, пока нет настоящих, загруженных откуда-либо
    NotesRepository getNotesExample(int count);

    //Сохранение даты в заметке
    void updateDateNote(Note note, long dateNew);

    void updateNote(int itemID, Note note);

    void deleteNote(Note note);
}
