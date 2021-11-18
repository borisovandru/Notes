package com.android.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        fontSizeSetting(view);
    }

    private void fontSizeSetting(View view) {
        TextView fontSize = view.findViewById(R.id.font_size);
        fontSize.setText(String.format(Locale.getDefault(), "%d", Settings.fontSize));
        Button btnChangeFont = view.findViewById(R.id.btn_font);
        btnChangeFont.setOnClickListener(v -> {
            Settings.fontSize = Integer.parseInt(fontSize.getText().toString());
            writeSettings();
        });
    }

    private void writeSettings() {
        // Специальный класс для хранения настроек
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(Settings.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // Настройки сохраняются посредством специального класса editor
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Settings.FONT_SIZE_KEY, Settings.fontSize);
        editor.apply();
    }
}
