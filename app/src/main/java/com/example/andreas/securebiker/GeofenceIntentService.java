package com.example.andreas.securebiker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
       // NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //nManager.notify(0, buildNotification().build());
    }

    public NotificationCompat.Builder buildNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // Definition des Alarmsignals
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        mBuilder.setSound(soundUri)
                .setContentText("Gefahrenstelle naht!")
                .setContentTitle("Alarm");

        Intent intent = new Intent(this, MainActivity.class);

        //intent.setAction(BROADCAST_ACTION);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Konstruktion eines künstlichen BackStack für cross-task-Navigation
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        //mBuilder.setContentIntent(pendingIntent);

        return mBuilder;
    }
}
