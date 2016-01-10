package com.example.andreas.securebiker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andreas.securebiker.Fragments.AllPreferencesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final int REQUESTCODE_SETTINGS = 1;
    private static final String NOT = "NOTIFICATIONS";

    // Settings variables
    // screen on/off
    private boolean keepScreenOn = false;
    // alert on/off
    private boolean alertEnabled = true;
    // geofence radius
    private int geofenceRadius;
    // alarm duration
    private int alarmDuration;
    // diameter of danger spot circle
    public static final int GEOFENCE_CIRCLE_RADIUS = 5;
    // timer for scheduling the AlertDialog
    private Timer timer;

    // refrence variables for GoogleAPIClient & Maps objects
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    // intial zoom level of the map camera
    private int intialCameraZoom = 16;
    private Location lastLocation;
    private LatLng pos;
    private Marker marker;
    private ArrayList<LatLng> latLngList = null;
    private ArrayList<Geofence> geofenceList = null;
    private ArrayList<Circle> geofenceCircles = null;

    // declaration of BroadcastReceiver, AsyncTask & AlertDialog fragment
    private AlarmBroadcastReceiver aB = null;
    private FileReaderTask task = null;
    private DialogFragment newFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initial checkup of gps system settings
        checkGPS();

        // loading the UI
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // loading the app settings
        loadPreferences();

        // initializing the AsnyncTask
        task = new FileReaderTask();

        // initializing ArrayList for storing geonfence circles
        geofenceCircles = new ArrayList<>();

        // setting up the BroadcastReceiver
        aB = new AlarmBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter((GeofenceIntentService.BROADCAST_ACTION));
        iFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(aB, iFilter);

        // creating the location request
        createLocationRequest();

        // initializing SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // creating an instance of GoogleAPIClient
        buildGoogleApiClient();
    }

    /**
     *
     * Methods concerning UI and Settings
     *
     */

    /**
     * Method for checking the gps system settings
     */
    private void checkGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // if GPS is disabled, the corresponding system settings activity will be started
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast toast = Toast.makeText(this, R.string.toast_activate_gps, Toast.LENGTH_LONG);
            toast.show();
            startActivity(intent);
        }
    }

    /**
     * Method to keep the screen on/off
     */
    private void setWindow(boolean b) {
        if (b)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Handles the action when the 'Back'-Button is pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        loadPreferences();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handles the OptionMenu when it is created
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                //open Settings-Activity
                runSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method for starting the SettingsActivity after pushing the settings button
     */
    private void runSettingsActivity() {
        Intent settings_intent = new Intent(this, SettingsActivity.class);
        startActivity(settings_intent);
    }


    /**
     * Method for loading the app settings
     */
    private void loadPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_all, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // radius of geofence
        geofenceRadius = sharedPrefs.getInt(AllPreferencesFragment.KEY_FENCES_RADIUS, 150);
        // enabling/disabling the alarm
        alertEnabled = sharedPrefs.getBoolean(AllPreferencesFragment.KEY_ALARMSWITCH, true);
        // alarm duration
        alarmDuration = Integer.parseInt(sharedPrefs.getString(AllPreferencesFragment.KEY_ALARMDIALOGTIMER, "0"));
    }

    /**
     * Method for checking updates on geofence radius and device screen setting
     *
     * @param gR
     * @param kSO
     */
    private void checkSettingsUpdate(int gR, boolean kSO) {
        if (gR != geofenceRadius) {
            TextView tV = (TextView) findViewById(R.id.dist_text);
            tV.setText(Integer.toString(gR) + " " + Integer.toString(geofenceRadius));
            getGeofenceID();
            removeGeofences(); // removing current geofences
            registerGeofences(); // re-registering geofences
        }
        if (kSO != keepScreenOn) {
            setWindow(keepScreenOn);
        }
    }

    /**
     * Methods concerning the lifecycle of the activity
     */

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        boolean kSO = keepScreenOn;
        int gR = geofenceRadius;
        loadPreferences();
        checkSettingsUpdate(gR, kSO);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // connecting GoogleApiClient to Google Play Services
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //nMgr.cancel(0);
        if (googleApiClient.isConnected()) {
            removeGeofences();
            stopLocationUpdates();
        }
        // closing the connection to Google Play Services
        googleApiClient.disconnect();
        // canceling TimerTask of AlertDialog if active
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // dismissing active AlertDialog and so preventing the storing of the dialog in outState
        if (newFragment != null)
            newFragment.dismiss();
        super.onSaveInstanceState(outState);
    }

    /**
     *
     * Methods concerning the map & location:
     *
     */

    /**
     * Method for creating an instance of GoogleAPIClient
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API) // integration of the LocationServiceAPI
                .build();
    }

    /**
     * Method for creating an LocationRequest
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000); // setting the location update rate
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //  requesting the most precise location possible
    }

    /**
     * Method for starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Method for stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    /**
     * Method for updating the map after a location update
     */
    private void updateUI() {
        pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        marker.setPosition(pos); // moving the marker to current location
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pos)); // moving the camera to current location
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        if (lastLocation != null) {
            // getting the coordinates of the lastLocation
            pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Here I am!"));
            // defining the camera settings
            cameraPosition = new CameraPosition.Builder()
                    .target(pos)
                    .zoom(intialCameraZoom)
                    .build();
            // moving the camera to current location
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        // starting the location updates
        startLocationUpdates();

        // initializing and registering the Geofences
        task.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // stoping location updates
        stopLocationUpdates();

        Log.e("Connection Suspended", "Connection to Google Play Services suspended!");
        Toast toast = Toast.makeText(this, R.string.googleapi_connection_suspended, Toast.LENGTH_LONG);
        toast.show();

        // invoking connect() to reestablish connection
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Connection Failed", "Connection to Google Play Services failed!");
        Toast toast = Toast.makeText(this, R.string.googleapi_connection_failed, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        updateUI();
    }


    /**
     *
     * Methods concerning geofencing
     *
     */

    /**
     * Method for building and returning a GeofencingRequest
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // specifying how the geofences are triggered
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // specifying the list of geofences to be monitored
        builder.addGeofences(geofenceList);

        return builder.build();
    }

    /**
     * Method for issuing an PendingIntent to GeofenceIntentService whenever a geofence-event occurs for the
     * current list of geofences
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceIntentService.class);
        /*
        intent.putExtra(TIME, alarmDuration);
        intent.putExtra(ALARM_ENABLED,alertEnabled);
        intent.putExtra(SOUND_ENABLED, soundEnabled);
        intent.putExtra(VIBRATION_ENABLED,vibrationEnabled);
        */

        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    /**
     * Method for registering the geofences with the Google Play Services
     */
    private void registerGeofences() {
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        );
    }

    /**
     * Method for removing geofences from the Google Play Services
     */
    private void removeGeofences() {
        getGeofenceID();
        ArrayList<String> ids = getGeofenceID();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, ids);
    }

    /**
     * Method for extracting all RequestID's from geofenceList
     */
    private ArrayList<String> getGeofenceID() {
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < geofenceList.size(); i++) {
            String id = geofenceList.get(i).getRequestId();
            ids.add(id);
        }
        return ids;
    }

    /**
     * BroadcastReceiver for receiving intents from the GeofenceIntentService
     */
    public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver {

        public AlarmBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //buildNotification(alarmDuration, soundEnabled, vibrationEnabled); // Notification-Versand
//            if (vibrationEnabled)
//                vibrateAlarm();
//            if (soundEnabled)
//                playAlarmSound();
            if (newFragment == null && alertEnabled)
                showAlarmDialog();
        }

        /**
         * Method for displaying AlertDialog
         */
        public void showAlarmDialog() {
            newFragment = new AlarmDialogFragment();
            try {
                newFragment.show(getSupportFragmentManager(), "alarm");
            } catch (IllegalStateException e) {
                return;
            }
            timer = new Timer();
            timer.schedule(new TimerTask() { // TimerTask for the auto-closing of the AlertDialog
                public void run() {
                    try {
                        newFragment.dismiss();
                        // NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        // nMgr.cancel(0);
                    } catch (IllegalStateException e) {
                    } finally {
                        newFragment = null;
                    }

                }
            }, (alarmDuration * 1000)); // runtime of AlertDialog
        }
    }


    /**
     * Methode zur Bildung und Versand von Warn-Notification mit Alarm-Sound


     public void buildNotification(int alarmDuration, boolean sound, boolean vibration) {
     // Notification-Gedöns
     Uri a = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_bicycle_bell);
     Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
     //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
     // Uri alarmSound = RingtoneManager.getDefaultUri(R.raw.Luft_Alarm);
     NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     NotificationCompat.Builder mBuilder;
     mBuilder = new NotificationCompat.Builder(this).setCategory(Notification.CATEGORY_ALARM);
     if (sound)
     mBuilder.setSound(a);
     // Setting the vibration pattern
     if (vibration) {
     long[] vibrationPattern = vibrationSixSeconds;
     switch (alarmDuration) {
     case THREE_SECONDS:
     vibrationPattern = vibrationThreeSeconds;
     case NINE_SECONDS:
     vibrationPattern = vibrationTenSeconds;
     case SIX_SECONDS:
     vibrationPattern = vibrationSixSeconds;
     }
     mBuilder.setVibrate(vibrationPattern);
     /*vibrationPattern[0] = 0L;
     for (int i = 1; i < vibrationPattern.length; i++) {
     vibrationPattern[i] = (long) 1000;
     }*
     }
     mNotificationManager.notify(0, mBuilder.build());
     }
     */


    /**
     * AsyncTask for importing the locations of danger spots
     */
    public class FileReaderTask extends AsyncTask<Void, CircleOptions, ArrayList<LatLng>> {

        // declaration of reading streams
        InputStream iS = null;
        InputStreamReader iSR = null;
        BufferedReader bR = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> latLngs) {
            // storing LatLng's of danger spots in MainActivity for further usage
            latLngList = latLngs;
            // initializing geofenceList
            getGeofences(latLngs);
            // registering geofences
            registerGeofences();
        }

        @Override
        protected void onProgressUpdate(CircleOptions... cO) {
            // displaying a danger spot on the map
            Circle c = mMap.addCircle(cO[0]);
            // adding the danger spot circle to the ArrayList
            geofenceCircles.add(c);
        }

        @Override
        protected ArrayList<LatLng> doInBackground(Void... params) {
            ArrayList<LatLng> latLngs = new ArrayList<>();  // ArrayList for storing LatLng's of danger spots
            String s = "";
            try {
                // initializing the reading streams
                iS = getResources().openRawResource(R.raw.examplepoints);
                iSR = new InputStreamReader(iS);
                bR = new BufferedReader(iSR);
                int i = 0;
                while ((s = bR.readLine()) != null) {
                    LatLng l = stringToLatLng(s); // converting String to LatLng object
                    CircleOptions c = getCircleOptions(l); // creating CircleOptions object
                    // Geofence a = getGeofence(i, l); // creating Geofence object
                    latLngs.add(l); // adding geofence to ArrayList
                    i++;
                    publishProgress(c); // transfering circle object to onProgressUpdate-method
                }
            } catch (IOException e) {
                Log.e("Import failed!", "Import of danger spot locations failed");
            }
            return latLngs;
        }

        /**
         * Method for converting a String into LatLng object
         *
         * @param s
         * @return
         */
        private LatLng stringToLatLng(String s) {
            String[] temp = s.split(";");
            // int num = Integer.parseInt(temp[0]);
            double lat = Double.parseDouble(temp[1]);
            double lon = Double.parseDouble(temp[2]);
            LatLng l = new LatLng(lat, lon);
            return l;
        }

        /**
         * Method for creating a CircleOptions object from LatLng object
         *
         * @param l
         * @return
         */
        private CircleOptions getCircleOptions(LatLng l) {
            return new CircleOptions()
                    .radius(GEOFENCE_CIRCLE_RADIUS)
                    .center(l)
                    .fillColor(Color.argb(100, 0, 0, 255))
                    .strokeWidth(0.1f);
        }

    }

    /**
     * Method for creating an Geofence-List from LatLng-List
     *
     * @param latLngs
     * @return
     */
    private void getGeofences(ArrayList<LatLng> latLngs) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        for (int i = 0; i < latLngs.size(); i++) {
            LatLng l = latLngs.get(i);
            Geofence a = new Geofence.Builder().setCircularRegion(l.latitude, l.longitude, geofenceRadius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setRequestId(Integer.toString(i)).build();
            geofences.add(a);
        }
        geofenceList = geofences;
    }
}
