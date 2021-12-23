package com.android.notes.ui.edit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.notes.R;
import com.android.notes.domain.Callback;
import com.android.notes.domain.FirestoreNotesRepository;
import com.android.notes.domain.Note;
import com.android.notes.ui.notes.INoteObserver;
import com.android.notes.ui.notes.Publisher;
import com.android.notes.ui.notes.PublisherGetter;
import top.defaults.colorpicker.ColorPickerPopup;

public class EditNoteFragmentBottomSheet extends BottomSheetDialogFragment implements INoteObserver {
    public static final String TAG = "EditNoteFragmentBottomSheet";
    private TextInputEditText dateInput;
    public final FirestoreNotesRepository firestoreNotesRepository = FirestoreNotesRepository.INSTANCE;
    private TextInputEditText titleInput;
    private TextInputEditText noteInput;
    private MaterialButton btnSave;
    private Publisher publisher;
    private LinearLayoutCompat colorSelector;
    private Toolbar toolbar;

    public static EditNoteFragmentBottomSheet newInstance() {
        return new EditNoteFragmentBottomSheet();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        publisher = ((PublisherGetter) context).getPublisher();
    }

    private void init(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        titleInput = view.findViewById(R.id.titleInput);
        noteInput = view.findViewById(R.id.noteInput);
        btnSave = view.findViewById(R.id.btnSave);
        colorSelector = view.findViewById(R.id.colorSelector);
        dateInput = view.findViewById(R.id.dateInput);
        colorSelector.setOnClickListener(v -> {
            new ColorPickerPopup.Builder(getContext())
                    .initialColor(Color.RED)
                    .enableBrightness(false)
                    .enableAlpha(false)
                    .okTitle("OK")
                    .cancelTitle("CANCEL")
                    .showIndicator(true)
                    .showValue(false)
                    .build()
                    .show(colorSelector, new ColorPickerPopup.ColorPickerObserver() {
                        @Override
                        public void onColorPicked(int color) {
                            colorSelector.setBackgroundColor(color);
                        }
                    });
        });

        dateInput.setOnClickListener(v -> {
            callDatePicker();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_bottom_sheet, container, false);
        init(root);

        if (getArguments() != null) {
            String CURRENT_NOTE = "CURRENT_NOTE";
            Note note = (Note) getArguments().getSerializable(CURRENT_NOTE);
            if (note != null) {
                toolbar.setTitle(R.string.edit_mode_title);
                titleInput.setText(note.getTitle());
                noteInput.setText(note.getNote());
                dateInput.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(note.getDateCreated())));
                colorSelector.setBackgroundColor(note.getColor());
            } else {
                toolbar.setTitle(R.string.add_mode_title);
            }

            btnSave.setOnClickListener(v -> {
                String titleSave = titleInput.getText().toString();
                String noteSave = noteInput.getText().toString();
                int color = ((ColorDrawable) colorSelector.getBackground()).getColor();
                long dateSave = 0;
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(dateInput.getText().toString());
                    dateSave = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Note saveNote = new Note(titleSave, noteSave, dateSave, color);
                if (note != null) {
                    firestoreNotesRepository.updateNote(note, saveNote, new Callback<Boolean>() {
                        @Override
                        public void onResult(Boolean value) {
                            publisher.startUpdate();
                            dismiss();
                        }
                    });
                } else {
                    firestoreNotesRepository.addNote(saveNote, new Callback<Boolean>() {
                        @Override
                        public void onResult(Boolean value) {
                            publisher.startUpdate();
                            dismiss();
                        }
                    });
                }
            });
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((BottomSheetDialog) getDialog()).getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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

    @Override
    public void updateAllNotes() {
    }
}
