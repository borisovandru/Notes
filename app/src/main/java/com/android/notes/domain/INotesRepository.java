package com.android.notes.domain;

import java.util.List;

public interface INotesRepository {

    //Получение заметок
    void getNotes(Callback<List<Note>> callback);

    //Получение заметки по индексу
    void getNote(int index, Callback<Note> callback);

    //Генерация заметок, пока нет настоящих, загруженных откуда-либо
    void getNotesExample(int count, Callback<List<Note>> callback);

    //Изменение заметки
    void updateNote(Note oldNote, Note note, Callback<Boolean> callback);

    //Удаление заметки
    void deleteNote(Note note, Callback<Boolean> callback);

    //Добавление заметки
    void addNote(Note note, Callback<Boolean> callback);

}
