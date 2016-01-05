package com.example.andreas.securebiker;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by Dominic on 28.11.2015.
 */
public class GeofenceIntentService extends IntentService {

    public static final String BROADCAST_ACTION = "com.example.andreas.securebiker";

    public GeofenceIntentService() {
        super("Thread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            //TODO TBD
        }

        // Get the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Intent i = new Intent();
            i.setAction(BROADCAST_ACTION);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }
}
