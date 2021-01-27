package com.example.myforegroundservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public static String StartTime = "";
    NotificationHelper notificationHelper;
    String EndTime = "";
    int min;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();
        notificationHelper= new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.d("GeofenceBroadcastReceiv",":onReceive error receiving geofencing event..");
        }

        List<Geofence>geofenceList= geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList){
            Log.d("GeofenceBroadcastReceiv",":onReceive:"+ geofence.getRequestId());
        }
        int transitionType= geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d("Geofence Entered","Entered");
                Toast.makeText(context,"Entering on selected zone",Toast.LENGTH_SHORT).show();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeNow = date.format(currentLocalTime);
                StartTime = localTimeNow;

                notificationHelper.sendHighPriorityNotification("Entry","Entering at "+StartTime+" on selected zone " +
                        exampleService.current_Location.toString(),MainActivity.class);
              //  getCurrentLocation(context);
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"In the selected zone",Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Dwell","In the selected zone",MainActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
                Date currentLocalTimeEnd = cal2.getTime();
                DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date2.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeEnd = date2.format(currentLocalTimeEnd);
                EndTime = localTimeEnd;
                notificationHelper.sendHighPriorityNotification("Exit","Exit at "+EndTime+" from the selected zone" +
                        exampleService.current_Location.toString(),MainActivity.class);
                onExit(context);

                Toast.makeText(context,"Exit from the selected zone",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onExit(Context context){
        Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
        Date currentLocalTime2 = cal2.getTime();
        DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date2.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        String localTimeLater = date2.format(currentLocalTime2);
        EndTime = localTimeLater;
        min = getMinutes(context, StartTime, EndTime);
        checkCondition(context, min);
    }

    private void checkCondition(Context context, int myMin)
    {
        notificationHelper.sendHighPriorityNotification("Minutes","Total Minutes "+Integer.toString(myMin),MainActivity.class);
        if (myMin < 3) {
            Toast.makeText(context, "No nearby...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Nearby Success...", Toast.LENGTH_SHORT).show();
            //   getNearByDetails(context,myLatlng,"@string/google_maps_key");
        }
    }

    private int getMinutes(Context context, String startTime, String endTime)
    {
        int myMin = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        try {
            Date date1 = simpleDateFormat.parse(startTime);
            Date date2 = simpleDateFormat.parse(endTime);
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            myMin = min;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Error ", e.getMessage());
        }
        return myMin;
    }

}
