package com.example.android.stiumate.Classes;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Scoruri> {
    @Override
    public int compare(Scoruri a, Scoruri b) {
        return -a.getScor().compareTo(b.getScor());
    }
}
