package com.android.notes.ui.ui.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.notes.R;

public class NotesFragment extends Fragment implements INotesClickable, INoteObserver {
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    private com.android.notes.ui.model.Notes myNotes;
    private View root;
    private boolean isLandscape;
    private com.android.notes.ui.model.Note currentNote;
    private NotesAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notes, container, false);
        Publisher.getInstance().add(this);
        Init();
        adapter.notifyDataSetChanged();
        isLandscape = isLandscape();
        return root;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentNote = (com.android.notes.ui.model.Note) savedInstanceState.getSerializable(CURRENT_NOTE);
        } else {
            currentNote = myNotes.getNotes().get(0);
        }
        if (isLandscape) {
            showNotesLand(currentNote);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(CURRENT_NOTE, currentNote);
        super.onSaveInstanceState(outState);
    }

    private void Init() {
        if (root != null) {
            androidx.recyclerview.widget.RecyclerView recyclerView = root.findViewById(R.id.recyclerViewNotes);
            com.android.notes.ui.servicess.DataHelper data = new com.android.notes.ui.servicess.DataHelper();
            myNotes = data.load();
            currentNote = myNotes.getNotes().get(myNotes.getNotes().size() - 1);
            adapter = new NotesAdapter(myNotes, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onNoteClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, myNotes.getNotes().get(position));
        currentNote = myNotes.getNotes().get(position);
        if (isLandscape) {
            showNotesLand(myNotes.getNotes().get(position));
        } else {
            showNotes(myNotes.getNotes().get(position));
        }
    }

    private void showNotesLand(com.android.notes.ui.model.Note current) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, current);
        com.android.notes.ui.ui.detail.DetailFragment detailFragment = new com.android.notes.ui.ui.detail.DetailFragment();
        detailFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.detail_land, detailFragment);  // замена фрагмента
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void showNotes(com.android.notes.ui.model.Note current) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, current);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.nav_detail, bundle);
    }

    @Override
    public void update() {
        com.android.notes.ui.servicess.DataHelper data = new com.android.notes.ui.servicess.DataHelper();
        myNotes = data.load();
        adapter.notifyDataSetChanged();
    }
}
