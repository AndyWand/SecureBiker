package com.example.andreas.securebiker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by Dominic on 28.11.2015.
 */
public class GeofenceIntentService extends IntentService {

    public static final String BROADCAST_ACTION = "com.example.andreas.securebiker";
    public static final String GEOFENCE_ID = "GEOFENCEID";


    public GeofenceIntentService() {
        super("Thread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       // boolean sound = intent.getBooleanExtra(MainActivity.SOUND, true);
       // boolean vibration = intent.getBooleanExtra(MainActivity.VIBRATION, true);
       // int time = intent.getIntExtra(MainActivity.TIME, 5);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //TODO TBD
        }
        // Get the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Get the Geofences that were triggered
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        int s = triggeringGeofences.size();
        String[] ids = new String[s];
        for (int i = 0; i < s; i++)
            ids[i] = triggeringGeofences.get(i).getRequestId();

        Intent i = new Intent();
        i.setAction(BROADCAST_ACTION);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.putExtra(GEOFENCE_ID, ids);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }


}
