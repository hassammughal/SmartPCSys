package com.example.samsung.smartpcsys.utils;

import android.app.Application;

public class SngltonClass extends Application {
    // Create the instance
    private static SngltonClass instance;

    public static SngltonClass get() { return instance; }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

}
