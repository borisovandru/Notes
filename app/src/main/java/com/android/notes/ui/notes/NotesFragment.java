package com.android.notes.ui.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notes.R;
import com.android.notes.model.Note;
import com.android.notes.model.NotesRepository;
import com.android.notes.servicess.NoteService;
import com.android.notes.ui.detail.DetailFragment;

public class NotesFragment extends Fragment implements INotesClickable, INotesLongClickable, INoteObserver {
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    private RecyclerView recyclerView;
    private boolean isLandscape;
    private Note currentNote;
    private NotesAdapter adapter;
    private Publisher publisher;
    private NotesViewModel notesViewModel;
    private NavController navController;
    public final NoteService noteService = NoteService.INSTANCE;

    public void onAttach(Context context) {
        super.onAttach(context);
        publisher = ((PublisherGetter) context).getPublisher(); // получим обработчика подписок
        publisher.subscribe(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        publisher.unsubscribe(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NotesAdapter(this, this);
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        notesViewModel.fetchNotes();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewNotes);
        recyclerView.setAdapter(adapter);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        isLandscape = isLandscape();
        notesViewModel.getNotesLiveData()
                .observe(getViewLifecycleOwner(), new Observer<NotesRepository>() {
                    @Override
                    public void onChanged(NotesRepository notes) {
                        adapter.clear();
                        adapter.addItems(notes);
                        adapter.notifyDataSetChanged();
                    }
                });
        return root;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentNote = (Note) savedInstanceState.getSerializable(CURRENT_NOTE);
        } else {
            currentNote = notesViewModel.noteService.getNotes().get(0);
        }
        if (isLandscape) {
            showNotesLand(currentNote);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentNote != null) {
            outState.putSerializable(CURRENT_NOTE, currentNote);
        }
    }

    @Override
    public void onNoteClick(int position) {
        currentNote = notesViewModel.noteService.getNotes().get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, currentNote);

        if (isLandscape) {
            showNotesLand(currentNote);
        } else {
            showNotes(currentNote);
        }
    }

    private void showNotesLand(Note current) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, current);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.detail_land, detailFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void showNotes(Note current) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, current);
        navController.navigate(R.id.nav_detail, bundle);
    }

    @Override
    public void updateAllNotes() {
        notesViewModel.fetchNotes();
    }

    @Override
    public void onNoteLongClick(int position) {
        String[] items = {"Изменить", "Удалить", "Подробнее.."};
        String title = notesViewModel.noteService.getNotes().get(position).getTitle();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        currentNote = notesViewModel.noteService.getNotes().get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, currentNote);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        navController.navigate(R.id.nav__item_edit, bundle);
                        break;
                    case 1:
                        noteService.deleteNote(currentNote);
                        notesViewModel.fetchNotes();
                        break;
                    case 2:
                        if (isLandscape) {
                            showNotesLand(currentNote);
                        } else {
                            showNotes(currentNote);
                        }
                        break;
                }
            }
        });
        builder.show();
    }
}
