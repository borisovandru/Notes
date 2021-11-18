package com.android.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class NoteDialogCreateFragment extends DialogFragment implements NoteFragment.Controller{

    private final int position;

    public NoteDialogCreateFragment(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Подключаем макет
        //setCancelable(false);
        return inflater.inflate(R.layout.fragment_note_dialog_create,null);
    }

    @Override
    public void saveResult(Note note, int position) {
        NoteListFragment noteListFragment = (NoteListFragment) getParentFragmentManager().findFragmentByTag("NOTES_LIST_FRAGMENT_TAG");
        assert noteListFragment != null;
        noteListFragment.addUpdateNote(note, position);
        dismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Note newNote = new Note("", "", Calendar.getInstance().getTime());
        int idView = R.id.dialog_create;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(idView, NoteFragment.newInstance(newNote, position, true),"NOTES_LIST_FRAGMENT_TAG").commit();
    }


}
