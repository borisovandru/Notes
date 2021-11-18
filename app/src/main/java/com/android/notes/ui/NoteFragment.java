package com.android.notes.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NOTE_ARG_PARAM = "NOTE_ARG_PARAM";
    private static final String POSITION_ARG_PARAM = "POSITION_ARG_PARAM";
    private static final String DIALOG_ARG_PARAM = "DIALOG_ARG_PARAM";

    // TODO: Rename and change types of parameters
    private com.android.notes.domain.Note note = null;
    private EditText eName;
    private EditText eDescription;
    private TextView tvDate;
    Calendar calendar;
    private TextView tvAuthor;
    private int position;
    private boolean isDialogCall;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param note Parameter 1.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(com.android.notes.domain.Note note, int position, boolean isDialogCall) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_ARG_PARAM, note);
        args.putInt(POSITION_ARG_PARAM, position);
        args.putBoolean(DIALOG_ARG_PARAM, isDialogCall);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(com.android.notes.R.layout.fragment_note, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        setHasOptionsMenu(true);
        eName = view.findViewById(com.android.notes.R.id.name_edit_text);
        eDescription = view.findViewById(com.android.notes.R.id.descriptions_edit_text);
        tvDate = view.findViewById(com.android.notes.R.id.date);
        tvAuthor = view.findViewById(com.android.notes.R.id.author);
        calendar = Calendar.getInstance();
        Button saveChanges = view.findViewById(com.android.notes.R.id.save_changes);

        saveChanges.setOnClickListener(v -> {
            Controller controller;
            if (isDialogCall) {
                controller = (Controller) getParentFragment();
            } else {
                controller = (Controller) getActivity();
            }
            assert controller != null;
            com.android.notes.domain.Note newNote = changeCreateNote();
            controller.saveResult(newNote, position);
        });

        tvDate.setOnClickListener(this::onClickDate);
        // установка обработчика выбора даты
    }

    @NonNull
    private com.android.notes.domain.Note changeCreateNote() {
        String name = eName.getText().toString();
        String description = eDescription.getText().toString();
        Date date = calendar.getTime();
        if (note != null) {
            note.setName(name);
            note.setDescription(description);
            note.setDate(date);
            return note;
        } else return new com.android.notes.domain.Note(name, description, date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteFragment.Controller");
        }
        assert getArguments() != null;
        if (getArguments().getParcelable(NOTE_ARG_PARAM) != null) {

            note = getArguments().getParcelable(NOTE_ARG_PARAM);
            position = getArguments().getInt(POSITION_ARG_PARAM);
            isDialogCall = getArguments().getBoolean(DIALOG_ARG_PARAM);

        } else throw new RuntimeException("Создайте фрагмент при помощи newInstance");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        eName.setText(note.getName());
        eDescription.setText(note.getDescription());
        tvDate.setText(note.getDate().toString());
        tvAuthor.setText(note.getAuthor());

    }

    private void onClickDate(View v) {
        new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    tvDate.setText(calendar.getTime().toString());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public interface Controller {
        void saveResult(com.android.notes.domain.Note note, int position);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(com.android.notes.R.menu.note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == com.android.notes.R.id.action_send) {
            Toast.makeText(getContext(), "Отправили заметку", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == com.android.notes.R.id.add_image) {
            Toast.makeText(getContext(), "Добавили картинку", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

