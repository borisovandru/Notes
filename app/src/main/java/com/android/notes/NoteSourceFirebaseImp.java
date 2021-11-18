package com.android.notes;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
1. Обеспечьте хранение данных приложения через Firestore.
 */
public class NoteSourceFirebaseImp implements NoteSource {
    private static final String NOTES_COLLECTION = "note";
    private static final String TAG = "NotesSourceFirebaseImpl";

    // База данных Firestore
    private final FirebaseFirestore store = FirebaseFirestore.getInstance();

    // Коллекция документов
    private final CollectionReference collection = store.collection(NOTES_COLLECTION);

    // Загружаемый список карточек
    private List<Note> notesData = new ArrayList<>();

    public NoteSourceFirebaseImp() {
    }

    @Override
    public NoteSource init(final NotesSourceResponse notesSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Заголовок»
        // На самом деле тут можно было писать как на уроке, но я долго искала ошибку
        // при подключении и остановилась на этом методе. С ошибкой это конечно никак не связано)
        collection.orderBy("name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task -> {
                    notesData = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                        Map<String, Object> doc = documentSnapshot.getData();
                        String id = documentSnapshot.getId();
                        assert doc != null;
                        Note noteData = NoteMapping.toNoteData(id, doc);

                        notesData.add(noteData);
                    }
                    notesSourceResponse.initialized(NoteSourceFirebaseImp.this);
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
        return this;
    }

    @Override
    public Note getNoteData(int position) {
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
    public void updateNoteData(int position, Note noteData) {
        String id = noteData.getId();
        if (id != null) {
            // Изменить документ по идентификатору
            collection.document(id).set(noteData);
        }
    }

    @Override
    public void addNoteData(Note noteData) {
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
        for (Note note : notesData) {
            String id = note.getId();
            assert id != null;
            collection.document(id).delete()
                    .addOnSuccessListener(command -> notesData.clear());
        }

    }
}
