package com.android.notes.ui.detail;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.notes.R;
import com.android.notes.model.Note;
import com.android.notes.servicess.NoteService;
import com.android.notes.ui.notes.Publisher;
import com.android.notes.ui.notes.PublisherGetter;

public class DetailFragment extends Fragment {
    private final String CURRENT_NOTE = "CURRENT_NOTE";
    public final NoteService noteService = NoteService.INSTANCE;
    private TextView titleView;
    private TextView noteView;
    private TextView dateCreatedView;
    private LinearLayoutCompat rect;
    private Note current;
    private MaterialCardView detailCard;
    private Publisher publisher;

    public void onAttach(Context context) {
        super.onAttach(context);
        publisher = ((PublisherGetter) context).getPublisher(); // получим обработчика подписок
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DetailViewModel detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        if (getArguments() != null) {
            Note note = (Note) getArguments().getSerializable(CURRENT_NOTE);
            titleView = root.findViewById(R.id.titleTV);
            noteView = root.findViewById(R.id.noteTV);
            dateCreatedView = root.findViewById(R.id.dateCreatedTV);
            rect = root.findViewById(R.id.rect);
            detailCard = root.findViewById(R.id.detailCard);
            if (note != null) {
                detailCard.setVisibility(View.VISIBLE);
                current = note;
                titleView.setText(note.getTitle());
                noteView.setText(note.getNote());
                dateCreatedView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(note.getDateCreated())));
                rect.setBackgroundColor(Color.parseColor(note.getColor()));

                dateCreatedView.setOnClickListener(v -> {
                    callDatePicker(note.getDateCreated());
                });
            } else {
                detailCard.setVisibility(View.GONE);
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

    private void callDatePicker(long currentDate) {
        // получаем текущую дату
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTimeInMillis(currentDate);
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
                        noteService.updateDateNote(current, calendar.getTimeInMillis());
                        dateCreatedView.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(current.getDateCreated())));
                        publisher.startUpdate();
                    }
                }, myYear, myMonth, myDay);
        datePickerDialog.show();
    }
}