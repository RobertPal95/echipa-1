package com.example.android.stiumate.Classes;

import java.util.Comparator;

/**
 * Created by AA on 24.04.2018.
 */

public class DateComparator implements Comparator<Feedbacks> {
    @Override
    public int compare(Feedbacks a, Feedbacks b) {
        return - a.getData().compareTo(b.getData());
    }
}