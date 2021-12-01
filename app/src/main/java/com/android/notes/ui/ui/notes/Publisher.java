package com.android.notes.ui.ui.notes;

import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private static Publisher instance;

    private final List<INoteObserver> observers;

    private Publisher() {
        observers = new ArrayList<>();
    }

    public void add(INoteObserver observer) {
        observers.add(observer);
    }

    public static Publisher getInstance() {
        if (instance == null) {
            instance = new Publisher();
        }
        return instance;
    }

    public void startUpdate() {
        for (INoteObserver observer : observers) {
            observer.update();
        }
    }
}
