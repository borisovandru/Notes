package com.android.notes.ui;

import android.content.res.Configuration;
import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.notes.R;


public class NotesDetailsActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_ditals);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        if (savedInstanceState == null) {
            NotesDetailsFragment details = new NotesDetailsFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, details).commit();
        }

    }
}