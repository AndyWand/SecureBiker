package com.example.andreas.securebiker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.andreas.securebiker.Fragments.AllPreferencesFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dominic on 28.11.2015.
 * IntentService to handle Geofence events
 */
public class GeofenceIntentService extends IntentService {

    public static final String BROADCAST_ACTION = "com.example.andreas.securebiker";

    // constants and variables for building vibration pattern
    public static final int THREE_SECONDS = 3;
    public static final long[] vibrationThreeSeconds = {0, 1000, 1000, 2000};
    public static final int SIX_SECONDS = 6;
    public static final long[] vibrationSixSeconds = {0, 1000, 1000, 1000, 1000, 2000};
    public static final int NINE_SECONDS = 10;
    public static final long[] vibrationTenSeconds = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 2000};

    // variables to store Settings
    //private int geofenceDiameter = 100;
    //private boolean alarmDialogOn = true;
    private int time = 6;
    private boolean vibrationEnabled = true;
    private boolean soundEnabled = true;

    public GeofenceIntentService() {
        super("Thread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // loading the app settings
        loadPreferences();
        /*
        boolean alarmEnabled = intent.getBooleanExtra(MainActivity.ALARM_ENABLED,true);
        boolean soundEnabled = intent.getBooleanExtra(MainActivity.SOUND_ENABLED, true);
        boolean vibrationEnabled = intent.getBooleanExtra(MainActivity.VIBRATION_ENABLED, true);
        int time = intent.getIntExtra(MainActivity.TIME, 6);
        */

        // loading geofence event from intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.e("Geofence Error", "Geofence event has error!");
        } else {
            // getting the transition type
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // creating an alarm defined by app settings
            if (vibrationEnabled)
                vibrateAlarm(time);
            if (soundEnabled)
                playAlarmSound(time);
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Intent i = new Intent();
                i.setAction(BROADCAST_ACTION);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        }
    }

    /**
     * Method for creating an vibration alarm
     */
    public void vibrateAlarm(int time) {
        long[] vibrationPattern = vibrationSixSeconds;
        switch (time) {
            case THREE_SECONDS:
                vibrationPattern = vibrationThreeSeconds;
                break;
            case NINE_SECONDS:
                vibrationPattern = vibrationTenSeconds;
                break;
            case SIX_SECONDS:
                vibrationPattern = vibrationSixSeconds;
            default:
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(vibrationPattern, -1); // -1 = no repeating vibration
    }

    /**
     * Method for creating an alarm sound
     */
    public void playAlarmSound(int time) {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm_bicycle_bell);
        mediaPlayer.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // TimerTask for the auto-canceling of alarm sound
            public void run() {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }, (time * 1000));
    }

    /**
     * Method for loading app settings to define the alarm
     */
    private void loadPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_all, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // radius of geofence
        // geofenceDiameter = sharedPrefs.getInt(AllPreferencesFragment.KEY_FENCES_RADIUS, 150);
        // enabling/disabling the alarm
        //alarmDialogOn = sharedPrefs.getBoolean(AllPreferencesFragment.KEY_ALARMSWITCH, true);
        // alarm duration
        time = Integer.parseInt(sharedPrefs.getString(AllPreferencesFragment.KEY_ALARMDIALOGTIMER, "0"));
        // vibration
        vibrationEnabled = sharedPrefs.getBoolean(AllPreferencesFragment.KEY_NOTIFI_MESSAGE_VIB, true);
        // ringtone
        soundEnabled = sharedPrefs.getBoolean(AllPreferencesFragment.KEY_ALARMSWITCH, true);
    }
}

