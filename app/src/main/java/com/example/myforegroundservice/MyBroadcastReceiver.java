package com.example.myforegroundservice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceHelper = new GeofenceHelper(context);
        Log.i("Receiver", "Broadcast received: " + action);

        if(action.equals("my.action.string")){
            String state = intent.getExtras().getString("extra");
            Double lat=intent.getExtras().getDouble("lat");
            Double lon=intent.getExtras().getDouble("lon");
            if(state.equals("exited"))
            {
                // add lat/lng geofence method
                LatLng latLng=new LatLng(lat,lon);
                addGeofence(context,latLng,100);
                Toast.makeText(context, "Lat/Long : "+lat+"/"+lon, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Add geofence here", Toast.LENGTH_SHORT).show();

            }else
                {

                }
        }
    }


    private void addGeofence(Context context,LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofenceRequest = geofenceHelper.getGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyBroadcast ","onSuccess..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage= geofenceHelper.getErrorCode(e);
                        Log.d("MyBroadcast","onFailure:" + errorMessage);
                    }
                });
    }


}
