package com.android.notes.ui.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.notes.domain.FirestoreNotesRepository;

public class NotesViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NotesViewModel(FirestoreNotesRepository.INSTANCE);
    }
}
