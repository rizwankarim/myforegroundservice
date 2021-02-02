package com.example.myforegroundservice;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.myforegroundservice.App.CHANNEL_ID;

public class LocationService extends Service {

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification();
        getCurrentLocation();
        return START_STICKY;
    }

    public void setNotification(){
        Intent myIntent= new Intent(this,MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,myIntent,0);
        Notification notification= new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Location Foreground")
                .setContentText("Your location is getting by application")
                .setSmallIcon(R.drawable.ic_androidicon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        //locationRequest.setInterval(3000);
        //locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationindex = locationResult.getLocations().size() - 1;
                            double current_lat = locationResult.getLocations().get(locationindex).getLatitude();
                            double current_long = locationResult.getLocations().get(locationindex).getLongitude();
                            LatLng myLatlng = new LatLng(current_lat, current_long);
                            addGeofence(myLatlng,200);

                            Log.d("Receiver", String.valueOf(current_lat) + "," + String.valueOf(current_long));
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofenceRequest = geofenceHelper.getGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MapsActivity","onSuccess..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage= geofenceHelper.getErrorCode(e);
                        Log.d("MapsActivity","onFailure:" + errorMessage);
                    }
                });
    }

    public void initData(){
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }
}
