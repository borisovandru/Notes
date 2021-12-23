package com.android.notes.ui.detail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.notes.R;
import com.android.notes.domain.MockNotesRepository;
import com.android.notes.domain.Note;

public class DetailFragment extends Fragment {
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    public final MockNotesRepository mockNotesRepository = MockNotesRepository.INSTANCE;
    private Note current;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        init(root);

        if (getArguments() != null) {
            Note note = (Note) getArguments().getSerializable(CURRENT_NOTE);
            TextView titleView = root.findViewById(R.id.titleTV);
            TextView noteView = root.findViewById(R.id.noteTV);
            TextView dateCreatedView = root.findViewById(R.id.dateCreatedTV);
            LinearLayoutCompat rect = root.findViewById(R.id.rect);
            MaterialCardView detailCard = root.findViewById(R.id.detailCard);
            if (note != null) {
                detailCard.setVisibility(View.VISIBLE);
                current = note;
                titleView.setText(note.getTitle());
                noteView.setText(note.getNote());
                dateCreatedView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(note.getDateCreated())));
                rect.setBackgroundColor(note.getColor());
            } else {
                detailCard.setVisibility(View.GONE);
            }
        }
        return root;
    }

    private void init(View view) {
        boolean isLandscape = isLandscape();
        if (!isLandscape) {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(v -> {
                requireActivity().onBackPressed();
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (current != null) {
            outState.putSerializable(CURRENT_NOTE, current);
        }
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLandscape()) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        }
    }
}