package com.android.notes.ui.utils;

public class NotesLogger {
    public static void printLog(String message) {
        android.util.Log.d("notesLog", message);
    }

    public static void printLog(String message, LoggerType loggerType) {
        switch (loggerType) {
            case INFO:
                android.util.Log.i("notesLog", message);
                break;
            case WARN:
                android.util.Log.w("notesLog", message);
                break;
            case DEBUG:
                android.util.Log.d("notesLog", message);
                break;
            case ERROR:
                android.util.Log.e("notesLog", message);
                break;
            case ASSERT:
                android.util.Log.wtf("notesLog", message);
                break;
        }
    }
}
