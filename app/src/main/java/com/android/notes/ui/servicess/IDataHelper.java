package com.android.notes.ui.servicess;

public interface IDataHelper {

    com.android.notes.ui.model.Notes load();

    com.android.notes.ui.model.Notes loadExample(int count);

    void saveDate(com.android.notes.ui.model.Note note, long dateNew);

}
