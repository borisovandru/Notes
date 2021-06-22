package com.android.notes.domain;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.android.notes.R;

import java.util.ArrayList;
import java.util.List;

public class CardSourceImpl implements CardSource {
    private final List<CardData> cardDataList;
    private final Resources resources;

    public CardSourceImpl(Resources resources) {
        this.resources = resources;
        cardDataList = new ArrayList<>(7);
    }

    public CardSourceImpl init() {
        String[] titles = resources.getStringArray(R.array.name);
        String[] descriptions = resources.getStringArray(R.array.description);
        int[] images = getImageArray();
        for (int i = 0; i < titles.length; i++)
            cardDataList.add(new CardData(titles[i], images[i], descriptions[i]));
        return this;
    }

    private int[] getImageArray() {
        TypedArray pictures = resources.obtainTypedArray(R.array.pictures);
        int size = pictures.length();
        int[] images = new int[size];
        for (int i = 0; i < size; i++)
            images[i] = pictures.getResourceId(i, 0);
        return images;
    }

    @Override
    public CardData getCardData(int position) {
        return cardDataList.get(position);
    }

    @Override
    public int size() {
        return cardDataList.size();
    }
}
