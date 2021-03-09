package com.example.geogeo;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import android.widget.Toast;



public class MyGeoPositisionService extends Service {
    private LocationManager locationManager;
    public static final String CHANNEL = "MyGEOPosition";
    public static final String INFOCurrent = "INFOCurrent";
    public static final String PERMISSION = "PERMISSION";
    String StatusGPS;
    String StatusNet;
    String LocationGPS;
    String LocationNet;
    String EnabledGPS;
    String EnabledNet;

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
        }catch (SecurityException e){

        }
        checkEnabled();
        String result="{\"geoinfo\":"+"{\"StatusGPS\":\""+StatusGPS+"\",\"StatusNet\":\""+StatusNet+"\",\"LocationGPS\":\""+LocationGPS+"\",\"LocationNet\":\""+LocationGPS+"\",\"EnabledGPS\":\""+EnabledGPS+"\",\"EnabledNet\":"+EnabledNet+"\"}}";
        Intent intentresult=new Intent(CHANNEL);
        intent.putExtra(INFOCurrent,result);
        intent.putExtra(PERMISSION,"mygeopos");
        sendBroadcast(intent);
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

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                StatusGPS = (String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                StatusNet = (String.valueOf(status));
            }
        }
    };
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            LocationGPS = (formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            LocationNet = (formatLocation(location));
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
    }
}
