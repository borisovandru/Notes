package com.android.notes;

import com.google.firebase.Timestamp;

import java.util.Map;

/*
  преобразует полученные данные из MAP в объект.
  есть более простые методики.
 */

public class NoteMapping {
    public static class Fields {

        public final static String NAME = "name";
        public final static String DESCRIPTION = "description";
        public final static String DATE = "date";
    }

    public static Note toNoteData(String id, Map<String, Object> doc) {

        Timestamp date = (Timestamp) doc.get(Fields.DATE);
        assert date != null;
        Note answer = new Note((String) doc.get(Fields.NAME),
                (String) doc.get(Fields.DESCRIPTION), date.toDate());
        answer.setId(id);
        return answer;
    }
}
