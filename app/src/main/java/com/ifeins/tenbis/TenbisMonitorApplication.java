package com.ifeins.tenbis;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * @author ifeins
 */

public class TenbisMonitorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
