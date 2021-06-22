package com.android.notes.domain;

public interface CardSource {
    CardData getCardData(int position);

    int size();
}