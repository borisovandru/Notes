package com.android.notes.domain;

/*
данный интерфейс можно заменить на Runnable, но мне потребовалась такая конструкция чтоб понять как работают обозреватели
 */
public interface NotesSourceResponse {
    // Метод initialized() будет вызываться, когда данные проинициализируются и будут готовы.
    void initialized(NoteSource noteSource);
}
