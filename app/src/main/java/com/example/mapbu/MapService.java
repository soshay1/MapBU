package com.example.mapbu;



import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import androidx.annotation.Nullable;
//import android.support.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import static com.example.mapbu.App.CHANNEL_ID;

public class MapService extends IntentService {
    //String CHANNEL_ID= "MapServiceChannel";
    private static final String TAG = "MapService";

    //private PowerManager.WakeLock wakeLock;

    public MapService() {
        super("MapService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        //        "ExampleApp:Wakelock");
        //wakeLock.acquire();
        // Log.d(TAG, "Wakelock acquired");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Example IntentService")
                    .setContentText("Running...")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");

        String input = intent.getStringExtra("inputExtra");

        for (int i = 0; i < 10; i++) {
            Log.d(TAG, input + " - " + i);
            SystemClock.sleep(1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        //wakeLock.release();
        //Log.d(TAG, "Wakelock released");
    }
}
/*import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MapService extends Service {

    public void startNotificationListener() {
        //start's a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //fetching notifications from server
                //if there is notifications then call this method
                ShowNotification();
            }
        }).start();
    }

    @Override
    public void onCreate() {
        startNotificationListener();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ShowNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getBaseContext(), "notification_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("content")
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build();
        notificationManager.notify(0, notification);
        //the notification is not showing

    }
}
    /*
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.content.ContentValues.TAG;

/*public class MapService extends Service {
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    boolean isGPS = false;

    public MapService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        createNotification("PULL THE LEVER, KRONK", this, "Your stop, is up next!");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);////////////////

        locationRequest = LocationRequest.create();/////
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);///////////////
        locationRequest.setInterval(1000); // 1 seconds ////////////
        locationRequest.setFastestInterval(1000); //1 seconds ///////////

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() { /////////////
            @Override //////////
            public void gpsStatus(boolean isGPSEnable) { //////////////
                // turn on GPS/////////////////
                isGPS = isGPSEnable;/////////////
            }/////////////
        });////////////
        locationCallback = new LocationCallback() { /////////////////////////////////
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        onLocationChanged(location);
                        /*wayLatitude = location.getLatitude();
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
                        }*//*
                    }
                }
            }
        };////////////////////


    }
    public void onLocationChanged(Location location){

    }
    private void getLocation() {
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);///////////
    }
    private void stoploc(){//////////////
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
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
}*/
