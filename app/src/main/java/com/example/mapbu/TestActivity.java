package com.example.mapbu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapbu.AppConstants;
import com.example.mapbu.GpsUtils;
import com.example.mapbu.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;///////////////////
    private LocationCallback locationCallback;/////////////////
    private android.widget.Button btnLocation;
    private TextView txtLocation;
    private android.widget.Button btnContinueLocation;
    private TextView txtContinueLocation;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;/////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtContinueLocation = (TextView) findViewById(R.id.txtContinueLocation);
        this.btnContinueLocation = (Button) findViewById(R.id.btnContinueLocation);
        this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        this.btnLocation = (Button) findViewById(R.id.btnLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);///

        locationRequest = LocationRequest.create();///
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);///
        locationRequest.setInterval(1000); // 1 seconds///
        locationRequest.setFastestInterval(1000); //1 seconds///

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {////////////////////////////
                // turn on GPS
                isGPS = isGPSEnable;////////////////////
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            txtContinueLocation.setText(stringBuilder.toString());
                            // if(stringBuilder.length()>40){
                            //    mFusedLocationClient.removeLocationUpdates(locationCallback);
                            // }
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        btnLocation.setOnClickListener(v -> {

            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isContinue){
                stoploc();
            }
            isContinue = false;
            getLocation();
        });

        btnContinueLocation.setOnClickListener(v -> {
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            isContinue = true;
            stringBuilder = new StringBuilder();
            getLocation();
        });
        for(int i=0; i<100; i++){
            Log.i("app","DOIN DOING DOIFJJSKDHFKLDSHFULSFD");
        }


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(TestActivity.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }
    private void stoploc(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(TestActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
}



/*package com.example.mapbu;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TestActivity extends AppCompatActivity {

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    //private LocationRequest locationRequest;
    //private LocationCallback locationCallback;
    private long UPDATE_INTERVAL = 1000;  /* 10 secs */
    /*private long FASTEST_INTERVAL = 1000; /* 2 sec */

    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startLocationUpdates();
        Log.i("app","turned on location updates?");
        //stopLocationUpdates();
    }*/

    // Trigger new location updates at interval
    /*protected void stopLocationUpdates(){
        getFusedLocationProviderClient(this).removeLocationUpdates(new LocationCallback());
    }*/
    /*protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }*/
    /*private void startLocationUpdates() {
        fusedLocationClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = null;

                for (Location location : locationResult.getLocations()) {

                    onLocationChanged(location);
                }

                stopLocationUpdates();
            }
        };
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        fusedLocationClient = null;
        locationRequest = null;
        locationCallback = null;
    }
    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.i("app",msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }*/
    /*
    private FusedLocationProviderClient mFusedLocationClient;
    //private TextView txtLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //this.txtLocation = (TextView) findViewById(R.id.txtLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double wayLatitude = location.getLatitude();
                        double wayLongitude = location.getLongitude();
                        Log.i("app", String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                    }
                }
            }
        };
        //if (mFusedLocationClient != null) {
         //   mFusedLocationClient.removeLocationUpdates(locationCallback);
        //}
    }

    ;/*
}
*/
