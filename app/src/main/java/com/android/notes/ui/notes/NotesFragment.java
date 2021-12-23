package com.android.notes.ui.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import com.android.notes.R;
import com.android.notes.domain.AccountInfo;
import com.android.notes.domain.Callback;
import com.android.notes.domain.FirestoreNotesRepository;
import com.android.notes.domain.Note;
import com.android.notes.ui.detail.DetailFragment;
import com.android.notes.ui.edit.EditNoteFragmentBottomSheet;
import com.android.notes.ui.notes.adapter.INotesClickable;
import com.android.notes.ui.notes.adapter.INotesLongClickable;
import com.android.notes.ui.notes.adapter.NoteDiffUtilCallback;
import com.android.notes.ui.notes.adapter.NotesAdapter;

public class NotesFragment extends Fragment implements INotesClickable, INotesLongClickable, INoteObserver {
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    private RecyclerView recyclerView;
    private boolean isLandscape;
    private Note currentNote;
    private NotesAdapter adapter;
    private Publisher publisher;
    private NotesViewModel notesViewModel;
    private NavController navController;
    private FloatingActionButton fabAdd;
    private TextView textInfo;
    private MaterialButton goAuthBtn;
    private final AccountInfo accountInfo = AccountInfo.INSTANCE;
    private Menu menu;

    public final FirestoreNotesRepository mockNotesRepository = FirestoreNotesRepository.INSTANCE;

    public void onAttach(Context context) {
        super.onAttach(context);
        // получим обработчика подписок
        publisher = ((PublisherGetter) context).getPublisher();
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
        notesViewModel = new ViewModelProvider(this, new NotesViewModelFactory()).get(NotesViewModel.class);
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewNotes);
        adapter = new NotesAdapter(this, this);
        recyclerView.setAdapter(adapter);
        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            currentNote = null;
            Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_NOTE, currentNote);

            //Вариант создания заметки через BottomSheetFragment
            EditNoteFragmentBottomSheet editNoteFragmentBottomSheet = EditNoteFragmentBottomSheet.newInstance();
            editNoteFragmentBottomSheet.setArguments(bundle);
            editNoteFragmentBottomSheet.show(getActivity().getSupportFragmentManager(), EditNoteFragmentBottomSheet.TAG);
        });

        textInfo = view.findViewById(R.id.text_info);
        ;
        goAuthBtn = view.findViewById(R.id.toAuthBtn);
        goAuthBtn.setOnClickListener(v -> {
            navController.navigate(R.id.nav__item_profile);
        });

        isLandscape = isLandscape();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        menu = toolbar.getMenu();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_find:
                    Toast.makeText(getActivity(), "Поиск заметок пока не работает", Toast.LENGTH_LONG).show();
                    return false;
                case R.id.action_sort:
                    Toast.makeText(getActivity(), "Сортировка заметок пока не работает", Toast.LENGTH_LONG).show();
                    return false;
            }
            return super.onOptionsItemSelected(item);
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        init(root);

        if (accountInfo.isEmpty()) {
            updateUI(false);
        } else {
            updateUI(true);
        }
        notesViewModel.fetchNotes();
        notesViewModel.getNotesLiveData()
                .observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        NoteDiffUtilCallback noteDiffUtilCallback =
                                new NoteDiffUtilCallback(adapter.getData(), notes);
                        DiffUtil.DiffResult noteDiffResult = DiffUtil.calculateDiff(noteDiffUtilCallback);
                        adapter.clear();
                        adapter.addItems(notes);
                        noteDiffResult.dispatchUpdatesTo(adapter);
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
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentNote = (Note) savedInstanceState.getSerializable(CURRENT_NOTE);
            if (isLandscape) {
                showNotesLand(currentNote);
            }
        } else {
            notesViewModel.repository.getNote(0, new Callback<Note>() {
                @Override
                public void onResult(Note value) {
                    currentNote = value;
                    if (isLandscape) {
                        showNotesLand(currentNote);
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentNote != null) {
            outState.putSerializable(CURRENT_NOTE, currentNote);
        }
    }

    //Показать заметку в альбомном режиме
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

    //Показать заметку в портретном режиме
    private void showNotes(Note current) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_NOTE, current);
        navController.navigate(R.id.nav_detail, bundle);
    }

    //Обновить данные в ресайкле
    @Override
    public void updateAllNotes() {
        notesViewModel.fetchNotes();
    }

    //Показать подробности заметки
    @Override
    public void onNoteClick(int position) {
        notesViewModel.repository.getNotes(new Callback<List<Note>>() {
            @Override
            public void onResult(List<Note> value) {
                currentNote = value.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CURRENT_NOTE, currentNote);
                if (isLandscape) {
                    showNotesLand(currentNote);
                } else {
                    showNotes(currentNote);
                }
            }
        });
    }

    //Показать контекстное меню при длительном нажатии по карточке
    @Override
    public void onNoteLongClick(int position) {
        notesViewModel.repository.getNotes(new Callback<List<Note>>() {
            @Override
            public void onResult(List<Note> value) {
                String[] items = {"Изменить", "Удалить", "Подробнее.."};
                currentNote = value.get(position);
                String title = currentNote.getTitle();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                Bundle bundle = new Bundle();
                bundle.putSerializable(CURRENT_NOTE, currentNote);
                builder.setTitle(title);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                //Вариант редактирования заметки через BottomSheetFragment
                                EditNoteFragmentBottomSheet editNoteFragmentBottomSheet = EditNoteFragmentBottomSheet.newInstance();
                                editNoteFragmentBottomSheet.setArguments(bundle);
                                editNoteFragmentBottomSheet.show(getActivity().getSupportFragmentManager(), EditNoteFragmentBottomSheet.TAG);
                                break;
                            case 1:
                                deleteAlert(currentNote);
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
        });
    }

    void updateUI(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
            textInfo.setVisibility(View.GONE);
            goAuthBtn.setVisibility(View.GONE);
            //Покажем пункты меню тулбара
            menu.findItem(R.id.action_find).setVisible(true);
            menu.findItem(R.id.action_sort).setVisible(true);
        } else {
            recyclerView.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
            textInfo.setVisibility(View.VISIBLE);
            goAuthBtn.setVisibility(View.VISIBLE);
            //Скроем пункты меню тулбара
            menu.findItem(R.id.action_find).setVisible(false);
            menu.findItem(R.id.action_sort).setVisible(false);
        }
    }

    private void deleteAlert(Note note) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_alert_delete)
                .setMessage("Действительно хотите удалить заметку '" + note.getTitle() + "'?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mockNotesRepository.deleteNote(note, new Callback<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                notesViewModel.fetchNotes();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }
}
