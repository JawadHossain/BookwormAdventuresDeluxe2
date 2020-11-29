package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * Subclass of the Application class which is required to get context statically.
 */

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

//https://stackoverflow.com/questions/2002288/static-way-to-get-context-in-android
public class GlobalApplication extends Application
{
    private static Context context;
    public static final String CHANNEL_ID = "High priority channel";

    public void onCreate()
    {
        super.onCreate();
        GlobalApplication.context = getApplicationContext();
        createNotificationChannels();
    }

    public static Context getAppContext()
    {
        return GlobalApplication.context;
    }

    private void createNotificationChannels()
    {
        /* Required only for SDK > Oreo*/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "High Priority Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            /* Customize notification channel*/
            notificationChannel.setDescription("This is the high priority channel");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
