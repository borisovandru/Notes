package com.android.notes.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.notes.model.NotesRepository;
import com.android.notes.servicess.NoteService;

public class NotesViewModel extends ViewModel {

    public final NoteService noteService = NoteService.INSTANCE;

    private final MutableLiveData<NotesRepository> notesLiveData = new MutableLiveData<>();

    public NotesViewModel() {
    }

    public void fetchNotes() {
        notesLiveData.setValue(noteService.getNotes());
    }

    public LiveData<NotesRepository> getNotesLiveData() {
        return notesLiveData;
    }
}
