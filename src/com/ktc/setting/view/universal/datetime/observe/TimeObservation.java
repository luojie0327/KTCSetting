package com.ktc.setting.view.universal.datetime.observe;

import android.content.Intent;

import java.util.ArrayList;

public class TimeObservation {

    private ArrayList<TimeObserver> mTimeObservers;

    public TimeObservation() {
        mTimeObservers = new ArrayList<>();
    }

    public void addObserver(TimeObserver timeObserver) {
        mTimeObservers.add(timeObserver);
    }

    public void removeObserver(TimeObserver timeObserver) {
        mTimeObservers.remove(timeObserver);
    }

    public void notifyObservers(Intent intent) {
        for (TimeObserver timeObserver : mTimeObservers) {
            timeObserver.update(intent);
        }
    }
}
