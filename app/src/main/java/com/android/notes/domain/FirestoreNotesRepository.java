package com.android.notes.domain;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FirestoreNotesRepository implements INotesRepository {

    public static final FirestoreNotesRepository INSTANCE = new FirestoreNotesRepository();

    private static final String NOTES_COLLECTION = "notes";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_DATE = "dateCreated";
    public static final String FIELD_COLOR = "color";

    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    @Override
    public void getNotes(Callback<List<Note>> callback) {
        fireStore.collection(NOTES_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        ArrayList<Note> result = new ArrayList<>();
                        for (DocumentSnapshot doc : documents) {
                            String title = doc.getString(FIELD_TITLE);
                            String note_ = doc.getString(FIELD_NOTE);
                            int color = doc.getLong(FIELD_COLOR).intValue();
                            String id = doc.getId();
                            Date date = doc.getDate(FIELD_DATE);
                            Note note = new Note(title, note_, date.getTime(), color);
                            note.setId(id);
                            result.add(note);
                        }
                        Collections.sort(result, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {
                                Long d1 = o1.getDateCreated();
                                Long d2 = o2.getDateCreated();
                                return d2.compareTo(d1);
                            }
                        });
                        callback.onResult(result);
                    }
                });
    }

    @Override
    public void getNote(int index, Callback<Note> callback) {

    }

    @Override
    public void getNotesExample(int count, Callback<List<Note>> callback) {

    }

    @Override
    public void updateNote(Note oldNote, Note note, Callback<Boolean> callback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(FIELD_TITLE, note.getTitle());
        data.put(FIELD_NOTE, note.getNote());
        data.put(FIELD_DATE, new Date(note.getDateCreated()));
        data.put(FIELD_COLOR, note.getColor());

        fireStore.collection(NOTES_COLLECTION)
                //.document(oldNote.getId()).set(data);
                .document(oldNote.getId()).update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onResult(true);
                    }
                });
    }

    @Override
    public void deleteNote(Note note, Callback<Boolean> callback) {
        fireStore.collection(NOTES_COLLECTION)
                .document(note.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onResult(true);
                    }
                });
    }

    @Override
    public void addNote(Note note, Callback<Boolean> callback) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(FIELD_TITLE, note.getTitle());
        data.put(FIELD_NOTE, note.getNote());
        data.put(FIELD_DATE, new Date(note.getDateCreated()));
        data.put(FIELD_COLOR, note.getColor());

        fireStore.collection(NOTES_COLLECTION)
                .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String id = task.getResult().getId();
                note.setId(id);
                callback.onResult(true);
            }
        });
    }
}
