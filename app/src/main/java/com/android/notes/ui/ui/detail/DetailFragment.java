package com.android.notes.ui.ui.detail;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailFragment extends Fragment {
    private final String CURRENT_NOTE = "CURRENT_NOTE";

    private TextView dateCreatedView;
    private com.android.notes.ui.model.Note current;

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(com.android.notes.R.layout.fragment_detail, container, false);
        if (root != null) {
            if (getArguments() != null) {
                com.android.notes.ui.model.Note note = (com.android.notes.ui.model.Note) getArguments().getSerializable(CURRENT_NOTE);
                current = note;
                android.widget.TextView titleView = root.findViewById(com.android.notes.R.id.titleTV);
                android.widget.TextView noteView = root.findViewById(com.android.notes.R.id.noteTV);
                dateCreatedView = root.findViewById(com.android.notes.R.id.dateCreatedTV);
                androidx.appcompat.widget.LinearLayoutCompat rect = root.findViewById(com.android.notes.R.id.rect);
                titleView.setText(note.getTitle());
                noteView.setText(note.getNote());
                dateCreatedView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(note.getDateCreated())));
                rect.setBackgroundColor(Color.parseColor(note.getColor()));

                dateCreatedView.setOnClickListener(v -> {
                    callDatePicker();
                });
            }
        }
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_NOTE, current);
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (isLandscape()) {
            NavController navController = Navigation.findNavController(requireActivity(), com.android.notes.R.id.nav_host_fragment);
            navController.navigateUp();
        }
    }

    private void callDatePicker() {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        int myYear = cal.get(Calendar.YEAR);
        int myMonth = cal.get(Calendar.MONTH);
        int myDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
                        Date d = null;
                        long milliseconds = 0;
                        try {
                            d = f.parse(editTextDateParam);
                            assert d != null;
                            milliseconds = d.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        com.android.notes.ui.servicess.DataHelper data = new com.android.notes.ui.servicess.DataHelper();
                        data.saveDate(current, milliseconds);
                        dateCreatedView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(current.getDateCreated())));
                        com.android.notes.ui.ui.notes.Publisher.getInstance().startUpdate();
                    }
                }, myYear, myMonth, myDay);
        datePickerDialog.show();
    }
}