package com.android.notes.ui.edit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.notes.R;
import com.android.notes.model.Note;
import com.android.notes.servicess.NoteService;

public class EditNoteFragment extends Fragment {
    private TextInputEditText dateInput;
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    public final NoteService noteService = NoteService.INSTANCE;
    private TextInputEditText titleInput;
    private TextInputEditText noteInput;
    private NavController navController;
    private MaterialButton btnSave;

    public EditNoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_note, container, false);
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        titleInput = root.findViewById(R.id.titleInput);
        noteInput = root.findViewById(R.id.noteInput);
        dateInput = root.findViewById(R.id.dateInput);
        btnSave = root.findViewById(R.id.btnSave);
        dateInput.setOnClickListener(v -> {
            callDatePicker();
        });
        if (getArguments() != null) {
            Note note = (Note) getArguments().getSerializable(CURRENT_NOTE);
            if (note != null) {
                titleInput.setText(note.getTitle());
                noteInput.setText(note.getNote());
                dateInput.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(note.getDateCreated())));
                btnSave.setVisibility(View.VISIBLE);
                btnSave.setOnClickListener(v -> {
                    String titleSave = titleInput.getText().toString();
                    String noteSave = noteInput.getText().toString();
                    long dateSave = 0;
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("dd.MM.yyyy").parse(dateInput.getText().toString());
                        dateSave = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Note saveNote = new Note(titleSave, noteSave, dateSave, "#6d4c41");
                    if (note != null) noteService.updateNote(note.getId(), saveNote);
                    navController.navigateUp();
                });

            } else {
                btnSave.setVisibility(View.GONE);
            }
        }
        return root;
    }

    private void callDatePicker() {
        // получаем текущую дату
        Calendar cal = Calendar.getInstance();
        int myYear = cal.get(Calendar.YEAR);
        int myMonth = cal.get(Calendar.MONTH);
        int myDay = cal.get(Calendar.DAY_OF_MONTH);
        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, (monthOfYear));
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateInput.setText(new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTimeInMillis()));
                    }
                }, myYear, myMonth, myDay);
        datePickerDialog.show();
    }
}
