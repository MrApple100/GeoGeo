package com.example.geogeo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class Geoservice extends Service{
    public static final String CHANNEL= "GEO";
    public static final String INFOCurrent = "INFOCurrent";
    public static final String PERMISSION = "PERMISSION";
    public static final String GOODCOORD = "GOODCOORD";
    private String Action;

    @Override
    public void onCreate() {
        Toast.makeText(getApplication(),"ServiceCreated",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplication(),"ServiceStarted",Toast.LENGTH_LONG);
        AnotherThread anotherThread;
        AnotherThreadlonlat anotherThreadlonlat;
        AnotherThreadbymygeopos anotherThreadbymygeopos;
        AnotherThreadupdateadded anotherThreadupdateadded;
        try {
            String permission = intent.getStringExtra(PERMISSION);
            if(permission.compareTo("city")==0){
            String wordofcity = intent.getStringExtra("city").trim();
            anotherThread = new AnotherThread(new URL("https://api.openweathermap.org/data/2.5/weather?q="+wordofcity+"&lang=ru&appid=11380ed4b5872057ec582d1289415365"));
            anotherThread.start();
            }else if(permission.compareTo("lonlat")==0){
                String lon=intent.getStringExtra("lon");
                String lat=intent.getStringExtra("lat");
                System.out.println(lon+" "+lat);
                anotherThreadlonlat = new AnotherThreadlonlat(new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=minutely,hourly&lang=ru&appid=11380ed4b5872057ec582d1289415365"),lon,lat);
                anotherThreadlonlat.start();
            }else if(permission.compareTo("bymygeopos")==0){
                Action = intent.getStringExtra("ACTION");
                String lon=intent.getStringExtra("lon");
                String lat=intent.getStringExtra("lat");
                anotherThreadbymygeopos = new AnotherThreadbymygeopos(new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=minutely,hourly&lang=ru&appid=11380ed4b5872057ec582d1289415365"));
                anotherThreadbymygeopos.start();
            }else if(permission.compareTo("updateadded")==0){
                String lon=intent.getStringExtra("lon");
                String lat=intent.getStringExtra("lat");
                System.out.println(lon+" "+lat);
                anotherThreadupdateadded = new AnotherThreadupdateadded(new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=minutely,hourly&lang=ru&appid=11380ed4b5872057ec582d1289415365"),lon,lat);
                anotherThreadupdateadded.start();
            }
        }catch(IOException e){
            e.getStackTrace();
        }
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
    public class AnotherThread extends Thread{
        URL url;
        AnotherThread(URL url){
            this.url=url;
        }
        @Override
        public void run() {
            synchronized (url) {
                String result;
                try{
                    Scanner inputstream = new Scanner((InputStream) url.getContent());
                    result="{\"gis\":" +inputstream.nextLine()+"}";
                    inputstream.close();
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                intent.putExtra(PERMISSION,"city");
                sendBroadcast(intent);
            }

        }
    }
    public class AnotherThreadlonlat extends Thread{
        URL url;
        String lon;
        String lat;
        AnotherThreadlonlat(URL url,String lon,String lat){
            this.url=url;
            this.lon=lon;
            this.lat=lat;
        }
        @Override
        public void run() {
            synchronized (url) {
                String result;
                try{
                    Scanner inputstream = new Scanner((InputStream) url.getContent());
                    System.out.println(url);
                    result="{\"gis\":" +inputstream.nextLine()+"}";
                    inputstream.close();
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                intent.putExtra(GOODCOORD,lon+"/"+lat);
                intent.putExtra(PERMISSION,"lonlat");
                sendBroadcast(intent);
            }

        }
    }
    public class AnotherThreadbymygeopos extends Thread{
        URL url;
        AnotherThreadbymygeopos(URL url){
            this.url=url;
        }
        @Override
        public void run() {
            synchronized (url) {
                String result;
                try{
                    Scanner inputstream = new Scanner((InputStream) url.getContent());
                    result="{\"gis\":" +inputstream.nextLine()+"}";
                    inputstream.close();
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                intent.putExtra(PERMISSION,"bymygeopos");
                intent.putExtra("ACTION",Action);
                sendBroadcast(intent);
            }

        }
    }
    public class AnotherThreadupdateadded extends Thread{
        URL url;
        String lon;
        String lat;
        AnotherThreadupdateadded(URL url,String lon,String lat){
            this.url=url;
            this.lon=lon;
            this.lat=lat;
        }
        @Override
        public void run() {
            synchronized (url) {
                String result;
                try{
                    Scanner inputstream = new Scanner((InputStream) url.getContent());
                    System.out.println(url);
                    result="{\"gis\":" +inputstream.nextLine()+"}";
                    inputstream.close();
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                intent.putExtra(GOODCOORD,lon+"/"+lat);
                intent.putExtra(PERMISSION,"updateadded");
                sendBroadcast(intent);
            }

        }
    }
}
