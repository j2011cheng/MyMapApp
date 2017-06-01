package com.example.chengj6157.mymapapp;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5.0f;
    private Location currentLoc;
    private static final float MY_LOC_ZOOM_FACTOR = 17.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng birth = new LatLng(32.798289, -117.155401);
        mMap.addMarker(new MarkerOptions().position(birth).title("Born Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birth));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapApp", "Fine Location Permission Failed");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapApp", "Course Location Permission Failed");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        mMap.setMyLocationEnabled(true);
    }

    public void getLocation(View v) {
        try {
            Log.d("MyMapApp", "getLocation: method called");
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled) Log.d("MyMapApp", "getLocation: GPS is enabled");

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) Log.d("MyMapApp", "getLocation: Network is enabled");

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("MyMapApp", "getLocation: No Provider is enabled");
            } else {
                canGetLocation = true;
                if (isGPSenabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMapApp", "Fine Location Permission Failed");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMapApp", "Course Location Permission Failed");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    }
                    Log.d("MyMapApp", "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    Log.d("MyMapApp", "getLocation: Network GPS update request success");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
                if (isNetworkEnabled) {
                    Log.d("MyMapApp", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    Log.d("MyMapApp", "getLocation: Network Network update request success");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }
            }
        }
        catch (Exception e){
            Log.d("MyMapApp", "delet this");
        }
    }

    public void dropMarker(String provider){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapApp", "Fine Location Permission Failed");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapApp", "Course Location Permission Failed");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        if(locationManager.getLastKnownLocation(provider) == null){
            Log.d("MyMapApp", "dropMarker: location = null");
        }
        else {
            Log.d("MyMapApp", "dropMarker: finding last known location");
            LatLng currentLoc = new LatLng(locationManager.getLastKnownLocation(provider).getLatitude(), locationManager.getLastKnownLocation(provider).getLongitude());
            Log.d("MyMapApp", "dropMarker: updating camera");
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLoc, MY_LOC_ZOOM_FACTOR);
            Log.d("MyMapApp", "dropMarker: creating circle");
            Circle circle = mMap.addCircle(new CircleOptions().center(currentLoc).radius(2).strokeColor(Color.BLUE).strokeWidth(1).fillColor(Color.WHITE));
        }
    }

    LocationListener locationListenerGps = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //output Log.d and Toast
            Log.d("MyMapApp", "locationListenerGps: onLocationChanged");
            //drop marker on map (dropMarker)
            dropMarker(LocationManager.GPS_PROVIDER);
            //disable network updates (LocationManager)

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //setup switch statement
            //case: LocationProvider.AVAILABLE output to Log.d or Toast
            //case: LocationProvider.OUT_OF_SERVICE request updates from NETWORK_PROVIDER
            //case: LocationProvider.TEMPORARILY_UNAVAILABLE request update from NETWORK_PROVIDER
            //case: default request updates from NETWORK_PROVIDER
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output Log.d and Toast

            //drop market on map (dropMarker)

            //relaunch request for network location update
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output Log.d or Toast
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
