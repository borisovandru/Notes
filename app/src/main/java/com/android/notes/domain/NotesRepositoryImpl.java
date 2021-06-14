package com.android.notes.domain;

import com.android.notes.R;

import java.util.ArrayList;
import java.util.List;

public class NotesRepositoryImpl implements NotesRepository{

    @Override
    public List<Notes> getNotes() {
        ArrayList<Notes> result = new ArrayList<>();
        /*result.add(new Notes(R.string.app_name, R.class))*/

        return result;

    }
}
