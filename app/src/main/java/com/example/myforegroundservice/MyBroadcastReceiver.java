package com.example.myforegroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.i("Receiver", "Broadcast received: " + action);

        if(action.equals("my.action.string")){
            String state = intent.getExtras().getString("extra");
            Double lat=intent.getExtras().getDouble("lat");
            Double lon=intent.getExtras().getDouble("lon");
            if(state.equals("exited"))
            {
                Toast.makeText(context, "Add geofence here", Toast.LENGTH_SHORT).show();
                // add lat/lng geofence method
            }else
                {
                }
        }
    }
}
