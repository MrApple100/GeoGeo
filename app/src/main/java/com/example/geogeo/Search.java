package com.example.geogeo;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSOutput;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.LogRecord;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Search extends Service{
    public static String CHANNEL="SEARCH";
    public static String INFO="listofcity";
    public static String ERROR="error";
    JSONObject jsonObjectsity;
    JSONArray jsonlistsity;
    Handler handler;
    AnotherThread anotherThread;
    String result=null;
    Boolean noresult=false;
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplication(),"SearchCreated",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplication(),"SearchStarted",Toast.LENGTH_LONG);
        String wordofcity=intent.getStringExtra("city").trim();
        int numchange=intent.getIntExtra("numchange",-1);
        anotherThread = new AnotherThread(wordofcity,numchange);
        if(ViewSearch.kolchanges==numchange){
            anotherThread.start();
        }
        handler = new Handler() {   // создание хэндлера
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (noresult) {
                    Intent intent = new Intent(CHANNEL);
                    intent.putExtra(INFO, Search.ERROR);
                    intent.putExtra("numchange", numchange);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(CHANNEL);
                    intent.putExtra(INFO, result);
                    intent.putExtra("numchange", numchange);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);
                }
            }
        };

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplication(),"SearchStopped",Toast.LENGTH_LONG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class AnotherThread extends Thread{
        String wordofcity;
        int numchange;
        AnotherThread(String wordofcity,int numchange){
            this.wordofcity=wordofcity;
            this.numchange=numchange;
        }
        @Override
        public void run() {
                synchronized (wordofcity) {
                        System.out.println(ViewSearch.kolchanges+"/"+numchange);

                        try {
                            System.out.println(wordofcity);
                            URL url = new URL("https://api.openweathermap.org/geo/1.0/direct?q=" + wordofcity + "&limit=16&appid=11380ed4b5872057ec582d1289415365");
                            Scanner inputstream = new Scanner((InputStream) url.getContent());
                            result = "{\"list\":" + inputstream.nextLine() + "}";
                        } catch (IOException eio) {
                            System.out.println("eeerrrreee");
                            eio.getStackTrace();
                            noresult = true;
                        }
                        handler.sendEmptyMessage(1);
                    }

        }
    }
}
