package com.android.notes.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import com.android.notes.domain.Callback;
import com.android.notes.domain.INotesRepository;
import com.android.notes.domain.Note;

public class NotesViewModel extends ViewModel {

    private final MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();

    public final INotesRepository repository;

    public NotesViewModel(INotesRepository repository) {
        this.repository = repository;
    }

    public void fetchNotes() {
        repository.getNotes(new Callback<List<Note>>() {
            @Override
            public void onResult(List<Note> value) {
                notesLiveData.setValue(value);
            }
        });
    }

    public LiveData<List<Note>> getNotesLiveData() {
        return notesLiveData;
    }
}
