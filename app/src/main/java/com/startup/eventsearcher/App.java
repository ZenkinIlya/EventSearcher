package com.startup.eventsearcher;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        Log.d("myMap", "App onTerminate()");
        super.onTerminate();
    }
}
