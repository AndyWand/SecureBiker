package com.example.andreas.securebiker;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private final int REQUESTCODE_SETTINGS = 1;
    private ArrayList<Geofence> geofenceList;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private CameraPosition cameraPosition;
    private LatLng pos;
    private Marker marker;

    private GoogleMap mMap;
    private HelperClass helper = new HelperClass();
    private ArrayList<LatLng> perils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Fügt GS aus exampledata.xml in das Array ein
        perils = helper.getExample();
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

        if (id == R.id.nav_sound) {
            // Handle the camera action
        } else if (id == R.id.nav_vibration) {

        } else if (id == R.id.nav_visual) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the corrent position and move the camera
        /**LatLng currentLocation = new LatLng();
         mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Sydney"));
         mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
         **/
    }

    /**
     * Methode zur Initalisierung der Liste mit Test-Geofences
     * Entnimmt die Punkte aus ArrayList:perils (in on Create initialisiert) und legt diese als
     * Geofence-Objekte an
     */
    private void initializeGeofences() {
        geofenceList = new ArrayList<>();
        for (LatLng gF : perils) {
            Geofence a = new Geofence.Builder().setCircularRegion(gF.latitude, gF.longitude, 150)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setRequestId("1").build();
            geofenceList.add(a);
        }
    }

    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        if (lastLocation != null) {
            // Auslesen der Koordinaten aus lastLocation
            pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Here I am!"));
            // Defintion von Kamera-Einstellungen
            cameraPosition = new CameraPosition.Builder().
                    target(pos)
                    .zoom(16)
                    .build();
            // Kamera wird auf aktuelle Position ausgerechnet
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //Einlesen der GS um diese dazustellen
            for (LatLng gF : perils) {
                mMap.addMarker(new MarkerOptions()
                        .position(gF)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.peril_marker)));
            }
        }
    }
}
