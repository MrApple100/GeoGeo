package com.example.geogeo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class Geoservice extends Service{
    public static final String CHANNEL= "GEO";
    public static final String INFOCurrent = "INFOCurrent";
    public static final String PERMISSION = "PERMISSION";
    @Override
    public void onCreate() {
        Toast.makeText(getApplication(),"ServiceCreated",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplication(),"ServiceStarted",Toast.LENGTH_LONG);
        AnotherThread anotherThread;
        AnotherThreadlonlat anotherThreadlonlat;
        try {
            String permission = intent.getStringExtra(PERMISSION);
            if(permission.compareTo("city")==0){
            String wordofcity = intent.getStringExtra("city").trim();
            anotherThread = new AnotherThread(new URL("https://api.openweathermap.org/data/2.5/weather?q="+wordofcity+"&lang=ru&appid=11380ed4b5872057ec582d1289415365"));
            anotherThread.start();
            }else if(permission.compareTo("lonlat")==0){
                String lon=intent.getStringExtra("lon");
                String lat=intent.getStringExtra("lat");
                anotherThreadlonlat = new AnotherThreadlonlat(new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&appid=11380ed4b5872057ec582d1289415365"));
                anotherThreadlonlat.start();
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
                    System.out.println(result);
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
        AnotherThreadlonlat(URL url){
            this.url=url;
        }
        @Override
        public void run() {
            synchronized (url) {
                String result;
                try{
                    Scanner inputstream = new Scanner((InputStream) url.getContent());
                    result="{\"gis\":" +inputstream.nextLine()+"}";
                    System.out.println("2222"+result);
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                intent.putExtra(PERMISSION,"id");
                sendBroadcast(intent);
            }

        }
    }
}
