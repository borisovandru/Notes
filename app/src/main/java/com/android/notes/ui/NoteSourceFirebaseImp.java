package com.android.notes.ui;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
1. Обеспечьте хранение данных приложения через Firestore.
 */
public class NoteSourceFirebaseImp implements com.android.notes.domain.NoteSource {
    private static final String NOTES_COLLECTION = "note";
    private static final String TAG = "NotesSourceFirebaseImpl";

    // База данных Firestore
    private final FirebaseFirestore store = FirebaseFirestore.getInstance();

    // Коллекция документов
    private final CollectionReference collection = store.collection(NOTES_COLLECTION);

    // Загружаемый список карточек
    private List<com.android.notes.domain.Note> notesData = new ArrayList<>();

    public NoteSourceFirebaseImp() {
    }

    @Override
    public com.android.notes.domain.NoteSource init(final com.android.notes.domain.NotesSourceResponse notesSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Заголовок»
        // На самом деле тут можно было писать как на уроке, но я долго искала ошибку
        // при подключении и остановилась на этом методе. С ошибкой это конечно никак не связано)
        collection.orderBy("name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task -> {
                    notesData = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        Map<String, Object> doc = documentSnapshot.getData();
                        String id = documentSnapshot.getId();
                        assert doc != null;
                        com.android.notes.domain.Note noteData = com.android.notes.domain.NoteMapping.toNoteData(id, doc);

                        notesData.add(noteData);
                    }
                    notesSourceResponse.initialized(NoteSourceFirebaseImp.this);
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
        return this;
    }

    @Override
    public com.android.notes.domain.Note getNoteData(int position) {
        return notesData.get(position);
    }

    @Override
    public int size() {
        if (notesData == null) {
            return 0;
        }
        return notesData.size();
    }

    @Override
    public void updateNoteData(int position, com.android.notes.domain.Note noteData) {
        String id = noteData.getId();
        if (id != null) {
            // Изменить документ по идентификатору
            collection.document(id).set(noteData);
        }
    }

    @Override
    public void addNoteData(com.android.notes.domain.Note noteData) {
        // Добавить документ
        collection.add(noteData).addOnSuccessListener(documentReference ->
                noteData.setId(documentReference.getId()));
    }

    @Override
    public void deleteNoteData(int position) {
        // Удалить документ с определённым идентификатором
        String id = notesData.get(position).getId();
        if (id != null) {
            collection.document(id).delete();
            notesData.remove(position);
        }
    }

    @Override
    public void clearNoteData() {
        for (com.android.notes.domain.Note note : notesData) {
            String id = note.getId();
            collection.document(id).delete()
                    .addOnSuccessListener(command -> notesData.clear());
        }

    }
}
