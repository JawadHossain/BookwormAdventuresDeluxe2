package com.example.bookwormadventuresdeluxe2;

/**
 * Subclass of the Application class which is required to get context statically.
 */

import android.app.Application;
import android.content.Context;

//https://stackoverflow.com/questions/2002288/static-way-to-get-context-in-android
public class GlobalApplication extends Application
{
    private static Context context;

    public void onCreate()
    {
        super.onCreate();
        GlobalApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return GlobalApplication.context;
    }
}
