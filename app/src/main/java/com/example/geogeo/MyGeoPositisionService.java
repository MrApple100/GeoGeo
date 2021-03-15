package com.example.geogeo;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;

import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class MyGeoPositisionService extends Service {
    private LocationManager locationManager;
    public static final String CHANNEL = "MyGEOPosition";
    public static final String INFOMYGEOPOSOTION = "INFOMYGEOPOSOTION";
    public static final String PERMISSION = "PERMISSION";
    String LocationGPS;
    String LocationNet;
    String EnabledGPS;
    String EnabledNet;
    String rezult;
    Intent intentresult = new Intent(CHANNEL);
    String imhereGPS;
    String imhereNet;
    int kol=0;
    int sec=0;

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        StringBuilder sbGPS = new StringBuilder();
        StringBuilder sbNet = new StringBuilder();
        try {


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 0, 100, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 0, 100, locationListener);

            imhereGPS = formatLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            imhereNet = formatLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }catch (SecurityException e){
            System.out.println("ERROR"+e.getLocalizedMessage());
            e.getStackTrace();
        }
        checkEnabled();
        AnotherThread anotherThread=new AnotherThread(intent);
        anotherThread.run();
        return START_NOT_STICKY;

    }
    @Override
    public void onDestroy() {
        Toast.makeText(getApplication(),"ServiceStopped",Toast.LENGTH_LONG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            try {
                showLocation(locationManager.getLastKnownLocation(provider));
            }catch (SecurityException e){

            }
        }

    };
    private void showLocation(Location location) {
        if (location == null) {
            return;
        }
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            LocationGPS = (formatLocation(location));
            kol++;
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            LocationNet = (formatLocation(location));
            kol++;
        }

    }
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format("%1$.4f/%2$.4f", location.getLatitude(), location.getLongitude());
    }
    private void checkEnabled() {
        EnabledGPS = ("" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        EnabledNet = ("" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        kol++;
    }
    public class AnotherThread extends Thread{
        Intent intent;
        AnotherThread(Intent intent){
            this.intent=intent;
        }
        @Override
        public void run() {
                try {
                    while (kol < 3 && sec != 1) {
                        sleep(1000);
                        sec++;
                    }
                }catch (Exception e){

                }
                   rezult = "{\"geoinfo\":" + "{\"LocationGPS\":\"" + imhereGPS + "\",\"LocationNet\":\"" + imhereNet + "\",\"EnabledGPS\":\"" + EnabledGPS + "\",\"EnabledNet\":" + EnabledNet + "\"}}";
                    intentresult.putExtra("INFOMYGEOPOSOTION",rezult);
                    if (intent.getStringExtra(PERMISSION).compareTo("mygeopos") == 0) {
                        intentresult.putExtra(PERMISSION, "mygeopos");
                    } else if (intent.getStringExtra(PERMISSION).compareTo("mygeoposNEW") == 0) {
                        intentresult.putExtra(PERMISSION, "mygeoposNEW");
                    }

            sendBroadcast(intentresult);

        }
    }
}
