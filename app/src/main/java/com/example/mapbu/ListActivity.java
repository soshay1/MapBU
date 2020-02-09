package com.example.mapbu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    MyRecyclerViewAdapter adapter;
    double smallLat;
    double largeLat;
    double smallLong;
    double largeLong;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //THIS ACTIVITY SHOULD ALSO USE LAT/LONG AND LOCATION BY INTENTS
        // data to populate the RecyclerView with
        ArrayList<String> routeNames = new ArrayList<>();
        smallLat=getIntent().getDoubleExtra("smallLat",0); //These extras are from the map activities
        smallLong=getIntent().getDoubleExtra("smallLong",0);
        largeLat=getIntent().getDoubleExtra("largeLat",0);
        largeLong=getIntent().getDoubleExtra("largeLong",0);
        routeNames.add("Downtown Center Leroy out / WS in");
        routeNames.add("West Side out / DCL in");
        routeNames.add("Rivera Ridge");
        routeNames.add("Leroy Southside");
        //routeNames.add("Goat");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.the_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, routeNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active or DON'T do this?????????????????? I need to push notifs :( What about battery?
        //if (mGoogleApiClient != null) {
        //    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //}
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent i;
        if(position==0){
            i = new Intent(ListActivity.this, MainActivity.class);
            i.putExtra("DCLOut", true); //I don't want to do another activity for the other way so here we are.
            i.putExtra("smallLat",smallLat);
            i.putExtra("smallLong",smallLong);
            i.putExtra("largeLat",largeLat);
            i.putExtra("largeLong",largeLong);
            startActivity(i);
        }
        if(position==1){
            i = new Intent(ListActivity.this, MainActivity.class);
            i.putExtra("DCLOut", false); //I don't want to do another activity for the other way so here we are.
            i.putExtra("smallLat",smallLat);
            i.putExtra("smallLong",smallLong);
            i.putExtra("largeLat",largeLat);
            i.putExtra("largeLong",largeLong);
            startActivity(i);
        }
        //if(position==2){
        //    Intent i = new Intent(ListActivity.this, MainActivity.class);
        //}

    }
    @Override
    public void onConnected (Bundle bundle){
        if(smallLat!=0){
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            //mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                Toast.makeText(this,"Location services are still on because a stop is selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionSuspended ( int i){
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult){
    }

    @Override
    public void onLocationChanged (Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //This is basic but maybe not the best way to solve this problem. What if the location isn't accurate enough?
        //What if the geometry is weird and this causes the app to not work as intended? I might want to do it all manually or
        //Only code SOME exceptions...
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(smallLat!=0){ //selected stop
            if(latLng.longitude>=smallLong && latLng.longitude<=largeLong &&latLng.latitude>=smallLat && latLng.latitude<=largeLat){
                Toast.makeText(this,"BITCHES WE ARE STOPPING",Toast.LENGTH_SHORT).show();

            }
        }

        //Place current location marker

        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));*/

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ListActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode,
                                             String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    protected synchronized void buildGoogleApiClient () {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}