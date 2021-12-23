package com.android.notes.domain;

public interface Callback<T> {
    void onResult(T value);
}
