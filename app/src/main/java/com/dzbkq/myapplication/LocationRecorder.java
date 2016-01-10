package com.dzbkq.myapplication;

import android.location.Location;

/**
 * Created by hotstu@github on 2015/12/22.
 */
public class LocationRecorder {
    private static LocationRecorder ourInstance = new LocationRecorder();
    private Location latestLocation = null;
    private long latestSaveTime = 0;

    public static LocationRecorder getInstance() {
        return ourInstance;
    }

    private LocationRecorder() {
    }

    public synchronized void save(Location l) {
        latestLocation = l;
        latestSaveTime = System.currentTimeMillis();
    }

    public synchronized Location get() {
        if (System.currentTimeMillis() - latestSaveTime > 30*1000)
            return null;
        else
            return latestLocation;
    }
}
