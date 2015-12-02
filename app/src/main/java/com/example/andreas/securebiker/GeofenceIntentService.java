package com.example.andreas.securebiker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            // TBD
        }

        // Get the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Get the Geofences that were triggered
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        String id = triggeringGeofences.get(0).getRequestId();

        Intent i = new Intent();
        i.setAction(BROADCAST_ACTION);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.putExtra(GEOFENCE_ID,id);

        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        buildNotification();
    }

    /**
     * Methode zur Bildung und Versand von Warn-Notification mit Alarm-Sound
     */
    public void buildNotification() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSound(alarmSound)
                .setCategory(Notification.CATEGORY_ALARM);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
