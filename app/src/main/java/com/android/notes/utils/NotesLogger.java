package com.android.notes.utils;

import android.util.Log;

public class NotesLogger {
    public static void printLog(String message) {
        Log.d("notesLog", message);
    }

    public static void printLog(String message, LoggerType loggerType) {
        switch (loggerType) {
            case INFO:
                Log.i("notesLog", message);
                break;
            case WARN:
                Log.w("notesLog", message);
                break;
            case DEBUG:
                Log.d("notesLog", message);
                break;
            case ERROR:
                Log.e("notesLog", message);
                break;
            case ASSERT:
                Log.wtf("notesLog", message);
                break;
        }
    }
}
