package com.example.mapbu;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    boolean isGPS = false;
    String stop ="";
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
        stop = getIntent().getStringExtra("stop");

        routeNames.add("Downtown Center Leroy out / WS in");
        routeNames.add("West Side out / DCL in");
        routeNames.add("Rivera Ridge");
        routeNames.add("Leroy Southside");
        //routeNames.add("Goat");
        Intent serviceIntent = new Intent(this, MapService.class);
        //serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.the_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, routeNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // 1 seconds
        locationRequest.setFastestInterval(1000); //1 seconds
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
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
                        onLocationChanged(location);
                    }
                }
            }
        };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED&&largeLat!=0) {
            getLocation(); //unsafe?
        }

    }
    private void getLocation() {
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Toast.makeText(this,"fjdskfdsf",Toast.LENGTH_SHORT).show();
    }
    private void stoploc(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //startService(new Intent(this,MapService.class));
        //stop location updates when Activity is no longer active or DON'T do this?????????????????? I need to push notifs :( What about battery?
        //if (mGoogleApiClient != null) {
        //    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
                //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                getLocation();
                Toast.makeText(this,"Location services are still on because a stop is selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionSuspended ( int i){
        Toast.makeText(this,"suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult){
        Toast.makeText(this,"FAILED",Toast.LENGTH_SHORT).show();
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
                createNotification("PULL THE LEVER, KRONK", this, "Your stop, "+stop+", is up next!"); // MUST GET STOP STRING
                stoploc();
                //Toast.makeText(this,"BITCHES WE ARE STOPPING",Toast.LENGTH_SHORT).show();

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
    private NotificationManager notifManager;
    public void createNotification(String aMessage, Context context, String contentText) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "channel_id"; // default_channel_id
        String title = "channel_title";// Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(contentText) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(contentText) // STOP BUS
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
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
            if(stop!=null&&stop!=""){
                i.putExtra("stop",stop);
            }
            startActivity(i);
        }
        //if(position==2){
        //    Intent i = new Intent(ListActivity.this, MainActivity.class);
        //}

    }
}