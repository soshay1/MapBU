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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;

import com.example.mapbu.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private LatLng JOHNSON_CITY = new LatLng(42.104311, -75.931283);
    private ArrayList<LatLng> Stop_LatLng = new ArrayList<LatLng>();
    private ArrayList<String> Stop_Strings = new ArrayList<String>();
    private ArrayList<Marker> Stop_Markers = new ArrayList<>();
    private Marker selectedMarker = null;
    double smallLat;
    double largeLat;
    double smallLong;
    double largeLong;
    boolean dontRepeatToastsForLocationServices = false;

    //Maybe use maps later on if I have time


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setTitle("Map Location Activity");

        boolean DCLOut = getIntent().getBooleanExtra("DCLOut",false);
        smallLat=getIntent().getDoubleExtra("smallLat",0); //These extras are from the map activities
        smallLong=getIntent().getDoubleExtra("smallLong",0);
        largeLat=getIntent().getDoubleExtra("largeLat",0);
        largeLong=getIntent().getDoubleExtra("largeLong",0);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        if(DCLOut){
            Stop_LatLng.add(new LatLng(42.090876, -75.973489));
            Stop_Strings.add("Physical Facilities");

            Stop_LatLng.add(new LatLng(42.095075, -75.975238));
            Stop_Strings.add("Denny's"); //GO TO DENNYSSSSSSSSSSSSSSSS

            //Start of DCL out
            Stop_LatLng.add(new LatLng(42.102223, -75.961692));
            Stop_Strings.add("Riverside and Ethel");

            Stop_LatLng.add(new LatLng(42.100528, -75.959290));
            Stop_Strings.add("Riverside and Elfred");

            Stop_LatLng.add(new LatLng(42.099310, -75.955227));
            Stop_Strings.add("Riverside and Columbus");

            Stop_LatLng.add(new LatLng(42.098050, -75.949329));
            Stop_Strings.add("Riverside and Margaret");

            Stop_LatLng.add(new LatLng(42.092519, -75.935848));
            Stop_Strings.add("Riverside and Beethoven");

            Stop_LatLng.add(new LatLng(42.095434, -75.930941)); //THIS ONE is going to be unique...
            Stop_Strings.add("Leroy and Laurel"); //Start of LEROY

            Stop_LatLng.add(new LatLng(42.095649, -75.926834));
            Stop_Strings.add("Leroy and Chestnut");

            Stop_LatLng.add(new LatLng(42.095431, -75.922624));
            Stop_Strings.add("Leroy and Murray"); //end of LEROY

            Stop_LatLng.add(new LatLng(42.092757, -75.920399)); //Is this really where that is? Check SPOT later if time
            Stop_Strings.add("Riverside and Front"); //tricky one

            Stop_LatLng.add(new LatLng(42.095062, -75.914237));
            Stop_Strings.add("UDC");

            //End of DCL out
            //Start of WS IN
            Stop_LatLng.add(new LatLng(42.098812, -75.915057));
            Stop_Strings.add("Hawley");

            Stop_LatLng.add(new LatLng(42.098871, -75.917603));
            Stop_Strings.add("Main and Front");

            Stop_LatLng.add(new LatLng(42.099671, -75.921747));
            Stop_Strings.add("Main and Murray");

            Stop_LatLng.add(new LatLng(42.100543, -75.924863));
            Stop_Strings.add("Main and Mather");

            Stop_LatLng.add(new LatLng(42.102270, -75.928407));
            Stop_Strings.add("Main and Cedar");

            Stop_LatLng.add(new LatLng(42.103245, -75.930116));
            Stop_Strings.add("Main and Clarke");

            Stop_LatLng.add(new LatLng(42.105442, -75.933987));
            Stop_Strings.add("Main and Schiller");

            Stop_LatLng.add(new LatLng(42.107241, -75.937321));
            Stop_Strings.add("Main and Helen");

            Stop_LatLng.add(new LatLng(42.109944, -75.942414));
            Stop_Strings.add("Main and Crary");

            Stop_LatLng.add(new LatLng(42.110851, -75.945491));
            Stop_Strings.add("Floral and Main"); //tricky

            Stop_LatLng.add(new LatLng(42.109116, -75.948739));
            Stop_Strings.add("Floral and Cleveland");

            Stop_LatLng.add(new LatLng(42.108598, -75.952368));
            Stop_Strings.add("Floral and Burbank");

            Stop_LatLng.add(new LatLng(42.108312, -75.954180));
            Stop_Strings.add("Floral and Willow");

            Stop_LatLng.add(new LatLng(42.108045, -75.956040));
            Stop_Strings.add("Floral and Roberts");

            Stop_LatLng.add(new LatLng(42.107795, -75.958276));
            Stop_Strings.add("Floral and Harrison");

            Stop_LatLng.add(new LatLng(42.107468, -75.960404));
            Stop_Strings.add("Floral and St. Charles");

            Stop_LatLng.add(new LatLng(42.107166, -75.963433));
            Stop_Strings.add("Floral and Cook");

            Stop_LatLng.add(new LatLng(42.088689, -75.973155));
            Stop_Strings.add("Academic A");

            Stop_LatLng.add(new LatLng(42.087327, -75.967487));
            Stop_Strings.add("Union");
        }
        else{
            Stop_LatLng.add(new LatLng(42.090876, -75.973489));
            Stop_Strings.add("Physical Facilities");

            Stop_LatLng.add(new LatLng(42.095075, -75.975238));
            Stop_Strings.add("Denny's"); //GO TO DENNYSSSSSSSSSSSSSSSS

            Stop_LatLng.add(new LatLng(42.107166, -75.963433));
            Stop_Strings.add("Floral and Cook");

            Stop_LatLng.add(new LatLng(42.107468, -75.960404));
            Stop_Strings.add("Floral and St. Charles");

            Stop_LatLng.add(new LatLng(42.107795, -75.958276));
            Stop_Strings.add("Floral and Harrison");

            Stop_LatLng.add(new LatLng(42.108045, -75.956040));
            Stop_Strings.add("Floral and Roberts");

            Stop_LatLng.add(new LatLng(42.108312, -75.954180));
            Stop_Strings.add("Floral and Willow");

            Stop_LatLng.add(new LatLng(42.108598, -75.952368));
            Stop_Strings.add("Floral and Burbank");

            Stop_LatLng.add(new LatLng(42.109116, -75.948739));
            Stop_Strings.add("Floral and Cleveland");

            Stop_LatLng.add(new LatLng(42.110851, -75.945491));
            Stop_Strings.add("Floral and Main"); //tricky

            Stop_LatLng.add(new LatLng(42.109944, -75.942414));
            Stop_Strings.add("Main and Crary");

            Stop_LatLng.add(new LatLng(42.107241, -75.937321));
            Stop_Strings.add("Main and Helen");

            Stop_LatLng.add(new LatLng(42.105442, -75.933987));
            Stop_Strings.add("Main and Schiller");

            Stop_LatLng.add(new LatLng(42.103245, -75.930116));
            Stop_Strings.add("Main and Clarke");

            Stop_LatLng.add(new LatLng(42.102270, -75.928407));
            Stop_Strings.add("Main and Cedar");

            Stop_LatLng.add(new LatLng(42.100543, -75.924863));
            Stop_Strings.add("Main and Mather");

            Stop_LatLng.add(new LatLng(42.099671, -75.921747));
            Stop_Strings.add("Main and Murray");

            Stop_LatLng.add(new LatLng(42.098871, -75.917603));
            Stop_Strings.add("Main and Front");

            Stop_LatLng.add(new LatLng(42.098812, -75.915057));
            Stop_Strings.add("Hawley");

            Stop_LatLng.add(new LatLng(42.095062, -75.914237));
            Stop_Strings.add("UDC");

            Stop_LatLng.add(new LatLng(42.100528, -75.959290));
            Stop_Strings.add("Riverside and Elfred");

            Stop_LatLng.add(new LatLng(42.099310, -75.955227));
            Stop_Strings.add("Riverside and Columbus");

            Stop_LatLng.add(new LatLng(42.098050, -75.949329));
            Stop_Strings.add("Riverside and Margaret");

            Stop_LatLng.add(new LatLng(42.092519, -75.935848));
            Stop_Strings.add("Riverside and Beethoven");

            Stop_LatLng.add(new LatLng(42.095434, -75.930941)); //THIS ONE is going to be unique...
            Stop_Strings.add("Leroy and Laurel"); //Start of LEROY

            Stop_LatLng.add(new LatLng(42.095649, -75.926834));
            Stop_Strings.add("Leroy and Chestnut");

            Stop_LatLng.add(new LatLng(42.095431, -75.922624));
            Stop_Strings.add("Leroy and Murray"); //end of LEROY

            Stop_LatLng.add(new LatLng(42.092757, -75.920399)); //Is this really where that is? Check SPOT later if time
            Stop_Strings.add("Riverside and Front"); //tricky one

            Stop_LatLng.add(new LatLng(42.092757, -75.920399)); //Is this really where that is? Check SPOT later if time
            Stop_Strings.add("Riverside and Front"); //tricky one

            Stop_LatLng.add(new LatLng(42.095431, -75.922624));
            Stop_Strings.add("Leroy and Murray"); //end of LEROY

            Stop_LatLng.add(new LatLng(42.095649, -75.926834));
            Stop_Strings.add("Leroy and Chestnut");

            Stop_LatLng.add(new LatLng(42.095434, -75.930941)); //THIS ONE is going to be unique...
            Stop_Strings.add("Leroy and Laurel"); //Start of LEROY

            Stop_LatLng.add(new LatLng(42.092519, -75.935848));
            Stop_Strings.add("Riverside and Beethoven");

            Stop_LatLng.add(new LatLng(42.098050, -75.949329));
            Stop_Strings.add("Riverside and Margaret");

            Stop_LatLng.add(new LatLng(42.099310, -75.955227));
            Stop_Strings.add("Riverside and Columbus");

            Stop_LatLng.add(new LatLng(42.100528, -75.959290));
            Stop_Strings.add("Riverside and Elfred");

            Stop_LatLng.add(new LatLng(42.102223, -75.961692));
            Stop_Strings.add("Riverside and Ethel");

            Stop_LatLng.add(new LatLng(42.088689, -75.973155));
            Stop_Strings.add("Academic A");

            Stop_LatLng.add(new LatLng(42.087327, -75.967487));
            Stop_Strings.add("Union");
        }


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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JOHNSON_CITY, 13));
        for (int i = 0; i < Stop_Strings.size(); i++) {
            Stop_Markers.add(mGoogleMap.addMarker(new MarkerOptions().position(Stop_LatLng.get(i)).title(Stop_Strings.get(i))));
            Stop_Markers.get(i).setTag(0);
        }
        mGoogleMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            if (clickCount == 1) {
                //     Toast.makeText(this,
                //         marker.getTitle() +
                //                " has been clicked " + clickCount + " times.",
                //        Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "You have clicked " + marker.getTitle() + ". Click this marker again to set your stop.", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedMarker == null || !selectedMarker.equals(marker)) {
                    Toast.makeText(this, marker.getTitle() + " has been set as your stop.", Toast.LENGTH_SHORT).show();
                    selectedMarker = marker;
                } else {
                    Toast.makeText(this, marker.getTitle() + " has been deselected as your stop. Select another stop or quit", Toast.LENGTH_SHORT).show();
                    selectedMarker = null;
                }
                if (selectedMarker != null) { //Sometimes the user does not select a stop :(
                    String myMarker = selectedMarker.getTitle();
                    int stopNum = Stop_Strings.indexOf(myMarker); //EXCEPTIONS include LEROY (LAUREL AND CHESTNUT)
                    // HAWLEY AND MAIN/FRONT
                    //PROBABLY EVERYTHING ON FLORAL but let's see lol
                    int referenceStop;
                    if (stopNum == 0) { //You cannot select the Union as your first stop so Physical facilities is index 0
                        referenceStop = Stop_Strings.size() - 1; //But the union is stop size-1 so it's fine
                    } else {
                        referenceStop = stopNum - 1; //Standard case
                    }
                    LatLng ourStopLatLng = Stop_LatLng.get(stopNum);
                    LatLng previousStopLatLng = Stop_LatLng.get(referenceStop);
                    if (ourStopLatLng.latitude > previousStopLatLng.latitude) {
                        smallLat = previousStopLatLng.latitude;
                        largeLat = ourStopLatLng.latitude;
                    } else if (ourStopLatLng.latitude < previousStopLatLng.latitude) {
                        smallLat = ourStopLatLng.latitude;
                        largeLat = previousStopLatLng.latitude;
                    } else {
                        Toast.makeText(this, "this bitch has the same lat wtf", Toast.LENGTH_SHORT).show();
                    }
                    if (ourStopLatLng.longitude > previousStopLatLng.longitude) {
                        smallLong = previousStopLatLng.longitude;
                        largeLong = ourStopLatLng.longitude;
                    } else if (ourStopLatLng.longitude < previousStopLatLng.longitude) {
                        smallLong = ourStopLatLng.longitude;
                        largeLong = previousStopLatLng.longitude;
                    } else {
                        Toast.makeText(this, "this bitch has the same long wtf", Toast.LENGTH_SHORT).show();
                    }
                    mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(1000);
                    //mLocationRequest.setFastestInterval(1000);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        if(!dontRepeatToastsForLocationServices){
                            dontRepeatToastsForLocationServices=true;
                            Toast.makeText(this, "Using location services because you selected a stop.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mGoogleApiClient != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                        if(dontRepeatToastsForLocationServices){
                            Toast.makeText(this, "No longer using location services.", Toast.LENGTH_SHORT).show();
                            dontRepeatToastsForLocationServices=false;
                        }
                    }
                }
            }


                // Toast.makeText(this,
                //        marker.getTitle() +
                //               " has been clicked " + clickCount + " times.",
                //       Toast.LENGTH_SHORT).show();
            }

            // Return false to indicate that we have not consumed the event and that we wish
            // for the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            return false;
        }
        protected synchronized void buildGoogleApiClient () {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }


     @Override
     public void onConnected (Bundle bundle){
         if(selectedMarker!=null){
             mLocationRequest = new LocationRequest();
             mLocationRequest.setInterval(1000);
             //mLocationRequest.setFastestInterval(1000);
             mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
             if (ContextCompat.checkSelfPermission(this,
                     Manifest.permission.ACCESS_FINE_LOCATION)
                     == PackageManager.PERMISSION_GRANTED) {
                 LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
            if(selectedMarker!=null){ //selected stop
                if(latLng.longitude>=smallLong && latLng.longitude<=largeLong &&latLng.latitude>=smallLat && latLng.latitude<=largeLat){
                    Toast.makeText(this,"BITCHES WE ARE STOPPING",Toast.LENGTH_SHORT).show();
                    createNotification("PULL THE LEVER, KRONK", this, "Your stop, "+selectedMarker.getTitle()+", is up next!");

// notificationId is a unique int for each notification that you must define
                    smallLat=0;
                    smallLong=0;
                    largeLat=0;
                    largeLong=0;
                    if (mGoogleApiClient != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                        Toast.makeText(this, "No longer using location services.", Toast.LENGTH_SHORT).show();
                        }

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
                                    ActivityCompat.requestPermissions(MainActivity.this,
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            //do whatever you want the 'Back' button to do
            //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
            //this.startActivity(new Intent(YourActivity.this,NewActivity.class));
            Intent i = new Intent(MainActivity.this, ListActivity.class);
            if(smallLat!=0) { //checking one instead of all. Bad practice?
                i.putExtra("smallLat", smallLat);
                i.putExtra("largeLat", largeLat);
                i.putExtra("smallLong",smallLong);
                i.putExtra("largeLong",largeLong);
            }
            startActivity(i);
        }
        return true;
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
}