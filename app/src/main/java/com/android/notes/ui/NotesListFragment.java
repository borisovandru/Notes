package com.android.notes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.notes.R;
import com.android.notes.domain.Notes;
import com.android.notes.domain.NotesRepository;
import com.android.notes.domain.NotesRepositoryImpl;

import java.util.List;

public class NotesListFragment extends Fragment {

    private NotesRepository notesRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notesRepository = new NotesRepositoryImpl();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout notesList = view.findViewById(R.id.notes_list_container);

        List<Notes> notes = notesRepository.getNotes();

        for (Notes notes1 : notes) {

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_notes, notesList, false);

            TextView notesName = itemView.findViewById(R.id.notes_name);

            notesName.setText(notes1.getName());

            notesList.addView(itemView);

        }

        /*NotesRepository notesRepository = new NotesRepositoryImpl();*/


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
