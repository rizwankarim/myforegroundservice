package com.example.myforegroundservice;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID="myForegroundService";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel nChannel= new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(nChannel);
        }
    }

}
