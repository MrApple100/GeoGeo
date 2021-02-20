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
    public static final String CHANNEL= "CHANNEL";
    public static final String INFOCurrent = "INFOCurrent";
    @Override
    public void onCreate() {
        Toast.makeText(getApplication(),"ServiceCreated",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplication(),"ServiceStarted",Toast.LENGTH_LONG);
        AnotherThread anotherThread;
        try {

            anotherThread = new AnotherThread(new URL("https://api.openweathermap.org/data/2.5/weather?q="+intent.getStringExtra("sity")+"&lang=ru&appid=11380ed4b5872057ec582d1289415365"));
            anotherThread.start();
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
                    result = "{\"gis\":" + inputstream.nextLine() + "}";
                    System.out.println(result);
                }catch(IOException eio){
                    result= eio.toString();
                }
                Intent intent=new Intent(CHANNEL);
                intent.putExtra(INFOCurrent,result);
                sendBroadcast(intent);
            }

        }
    }
}
