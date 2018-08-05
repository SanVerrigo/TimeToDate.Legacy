package com.verrigo.timetodate;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by repitch on 05.08.2018.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
