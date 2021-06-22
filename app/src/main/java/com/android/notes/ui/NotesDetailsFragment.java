package com.android.notes.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.notes.R;
import com.android.notes.domain.Notes;

public class NotesDetailsFragment extends Fragment {

    public static String ARG_NOTE = "ARG_NOTE";
    private Notes currentNote;

    public static NotesDetailsFragment newInstance(Notes note) {
        NotesDetailsFragment ndf = new NotesDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_NOTE, note);
        ndf.setArguments(bundle);
        return ndf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            currentNote = getArguments().getParcelable(ARG_NOTE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note_details, container, false);
        TextView nameTV = view.findViewById(R.id.name_details);
        TextView descriptionTV = view.findViewById(R.id.description_details);
        TextView dateTV = view.findViewById(R.id.date_details);
        TextView noteTV = view.findViewById(R.id.note_details);

        nameTV.setText(currentNote.getNoteName());
        descriptionTV.setText(currentNote.getNoteDescription());
        dateTV.setText(currentNote.getNoteDate());
        noteTV.setText(currentNote.getNote());
        return view;
    }
}

