package com.android.notes.domain;

import com.google.firebase.Timestamp;

import java.util.Map;

/*
  преобразует полученные данные из MAP в объект.
*/

public class NoteMapping {
    public static class Fields {

        public final static String NAME = "name";
        public final static String DESCRIPTION = "description";
        public final static String DATE = "date";
    }

    public static com.android.notes.domain.Note toNoteData(String id, Map<String, Object> doc) {

        Timestamp date = (Timestamp) doc.get(Fields.DATE);
        assert date != null;
        com.android.notes.domain.Note answer = new com.android.notes.domain.Note((String) doc.get(Fields.NAME),
                (String) doc.get(Fields.DESCRIPTION), date.toDate());
        answer.setId(id);
        return answer;
    }
}