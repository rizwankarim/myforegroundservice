package com.example.myforegroundservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    EditText Input;
    public static final int LOCATION_SERVICE_REQUEST=1;
    public static final int LOCATION_SERVICE_REQUEST_COARSE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Input= findViewById(R.id.input);


    }

    public void StartService(View v){

        boolean permissionAccessCoarseLocationApproved= ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;
        if(permissionAccessCoarseLocationApproved){
            startMethod();
        }

        else{
            ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_SERVICE_REQUEST);
        }
    }

    public void startMethod(){
        String input= Input.getText().toString();

        Intent serviceIntent= new Intent(this, exampleService.class);
        serviceIntent.putExtra("inputExtra",input);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(serviceIntent);
        }
        else{
            startService(serviceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(LOCATION_SERVICE_REQUEST==requestCode){
            boolean permissionAccessCoarseLocationApproved= ispermissionAccessCoarseLocationApproved();
            if(permissionAccessCoarseLocationApproved){
                startMethod();
            }
            else{
                Toast.makeText(this, "Permission is required to start service", Toast.LENGTH_SHORT).show();
            }
        }
        else if(LOCATION_SERVICE_REQUEST_COARSE==requestCode){
            startMethod();
        }

        else if (requestCode == 10002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                startMethod();
                //Toast.makeText(this,"You can add geofences...",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Background location access is necessary for geofences to trigger...",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean ispermissionAccessCoarseLocationApproved() {
        boolean check = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED;
        if(check){
            checkBackground();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_SERVICE_REQUEST_COARSE);
        }

        return check;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void StopService(View v){
        Intent serviceIntent= new Intent(this, exampleService.class);
        stopService(serviceIntent);
    }

    public void checkBackground() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                startMethod();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 10002);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 10002);
                }
            }
        }
    }
}