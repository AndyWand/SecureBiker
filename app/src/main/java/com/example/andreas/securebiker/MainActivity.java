package com.example.andreas.securebiker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    String s;
    private final int REQUESTCODE_SETTINGS = 1;
    private static final String NOT = "NOTIFICATIONS";

    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private Location lastLocation;
    private LatLng pos;
    private LocationRequest locationRequest;
    private Marker marker;
    private ArrayList<Geofence> geofenceList;
    private AlarmBroadcastReceiver aB;
    private ArrayList<Circle> geofencePufferList;
    private ArrayList<String> currentGeofences;
    private ArrayList<LatLng> ltlng;
<<<<<<< HEAD
=======
    private FileReaderTask task = null;
    private boolean alarmDialogOn = false;
>>>>>>> refs/heads/pr/2

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // GUI-Gedöns
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        geofenceList = new ArrayList();
        geofencePufferList = new ArrayList<>();
        currentGeofences = new ArrayList<>();

        // Initialisieren und ausführen der FileReaderTask zum Import der TestLocations für das Geofencing
        //task = new FileReaderTask();
        //task.execute();

        // BroadcastReceiver-Gedöns
        aB = new AlarmBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter((GeofenceIntentService.BROADCAST_ACTION));
        iFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(aB, iFilter);

        // Initalisierung des Location Request
        createLocationRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
            //turn sound on/off
        if (id == R.id.nav_sound) {
           //turn vibration on/off
        } else if (id == R.id.nav_vibration) {
            //turn visual alert on/off
        } else if (id == R.id.nav_visual) {
            DrawerLayout drawer = (DrawerLayout) findViewById(id);
      /**      ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                    R.drawable.ic_info_black_24dp, // nav menu toggle icon
                    R.string.app_name, // nav drawer open - description for
                    // accessibility
                    R.string.app_name // nav drawer close - description for
                    // accessibility
            );
       **/
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Location Updates stoppen, um Batterie zu stoppen
        // Verbindung zu GooglePlayServices wird nicht unterbrochen
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Wiederaufnahme der Location Updates, siehe @onPause()
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();  // Verbindet den Client mit Google Play Services
    }

    @Override
    protected void onStop() {
        // Entfernt Marker zur aktuellen Position
        if (!(marker == null)) {
            marker.remove();
        }
        // Entfernt aktuelle Puffer um Gefahrenstellen
        for (int i = 0; i < geofencePufferList.size(); i++)
            geofencePufferList.get(i).remove();
        googleApiClient.disconnect();   //schließt die Verbindung zu Google Play Services
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Methode zur Erzeugung der Google Play Service Api Instanz
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API) //Integration des Location Service
                .build();
    }

    /**
     * Methode zur Erzeugung eines Location Request
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000); // Intervall, in dem Updates zur Location empfangen werden sollen
        locationRequest.setFastestInterval(10000); // Zur Vermeidung von Komplikationen in Zusammenhang mit anderen Apps, die parallel in noch schnellerem Intervall LocationUpdates empfangen: Definition eines Intervall-Oberlimits
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Höchst mögliche Präzision der zu ermittelnden Location
    }

    /**
     * Methode zum Start des Empfangs von Location Updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Methode zum Stoppen des Empfangs von Location Updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    /**
     * Methode zur Aktualisierung der GUI/Map
     */
    private void updateUI() {
        pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        marker.setPosition(pos);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        if (lastLocation != null) {
            // Auslesen der Koordinaten aus lastLocation
            pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Here I am!"));
            // Defintion von Kamera-Einstellungen
            cameraPosition = new CameraPosition.Builder()
                    .target(pos)
                    .zoom(16)
                    .build();
            // Kamera wird auf aktuelle Position ausgerechnet
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        // Location-Updates werden gestartet
        startLocationUpdates();

        // Initalisierung und Registrierung der Geofences
        initializeGeofences();
        registerGeofences();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Die Verbindung zur Google Play Services ist unterbrochen worden
        // LocationUpdates werden ausgesetzt
        stopLocationUpdates();
        // Aufruf von connect(), um Verbindung wieder aufzubauen
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        CharSequence text = "Verbindungsaufbau gescheitert!";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        updateUI();
    }


    /**
     *
     * Ab hier folgt Geofencing-Gedöns
     *
     */


    /**
     * Methode zur Initalisierung der Liste mit Test-Geofences
     */
    private void initializeGeofences() {
<<<<<<< HEAD
        HelperClass help = new HelperClass();
        ltlng = help.getExample();
        LatLng gF1 = new LatLng(51.5221335, 7.2802826);
        geofenceList = new ArrayList<>();
        Geofence a = new Geofence.Builder().setCircularRegion(gF1.latitude, gF1.longitude, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setRequestId("1").build();
        geofenceList.add(a);
=======

        for (int i = 0; i < ltlng.size(); i++) {
            LatLng l = ltlng.get(i);
            Geofence a = new Geofence.Builder().setCircularRegion(l.latitude, l.longitude, 150)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setRequestId(Integer.toString(i)).build();
            geofenceList.add(a);
        }
>>>>>>> refs/heads/pr/2
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void registerGeofences() {
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        );
    }

    /**
     * BroadcastReceiver zum Empfang von Nachrichten vom GeofenceIntentService
     */
    public class AlarmBroadcastReceiver extends BroadcastReceiver {

        public AlarmBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String[] ids = intent.getStringArrayExtra(GeofenceIntentService.GEOFENCE_ID);
            for (int i = 0; i < ids.length; i++) {
                LatLng l = ltlng.get(i);
                Circle c = mMap.addCircle(new CircleOptions()
                        .radius(150)
                        .center(l)
                        .fillColor(Color.argb(100, 0, 0, 255))
                        .strokeWidth(0.1f));
                geofencePufferList.add(c);
                currentGeofences.add(ids[i]);
            }
        }

        /**
         * Methode zur Darstellung des Warn-Dialogs
         */
        public void showAlarmDialog() {
            DialogFragment newFragment = new AlarmDialogFragment();
            try {
                newFragment.show(getSupportFragmentManager(), "alarm");
            } catch (IllegalStateException e) {
                return;
            }
        }


    }

    /**
     * Eigene AsyncTask-Klasse zum Import von Geofence-Locations
     */
    public class FileReaderTask extends AsyncTask<Void, Void, ArrayList<LatLng>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<LatLng> doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> latLngs) {
            ltlng = latLngs;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
