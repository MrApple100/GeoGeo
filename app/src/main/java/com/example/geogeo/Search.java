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

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.places.PlacesClient;
import com.algolia.search.saas.places.PlacesQuery;

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
    JSONObject jsonObjectsity;
    JSONArray jsonlistsity;
    android.os.Handler handler;


    public void onCreate() {
        Toast.makeText(getApplication(),"SearchCreated",Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplication(),"SearchStarted",Toast.LENGTH_LONG);
        AnotherThread anotherThread;
        String wordofcity=intent.getStringExtra("city");
        int kolchanges=intent.getIntExtra("numchange",0);
        System.out.println("++++"+wordofcity);
        anotherThread = new AnotherThread(wordofcity,kolchanges);
        anotherThread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
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
                String result=null;
                Boolean noresult=false;
                synchronized (wordofcity) {
                    try {
                        System.out.println(wordofcity);
                        URL url = new URL("https://api.openweathermap.org/geo/1.0/direct?q=" + wordofcity + "&limit=5&appid=11380ed4b5872057ec582d1289415365");
                        Scanner inputstream = new Scanner((InputStream) url.getContent());
                        result = "{\"list\":" + inputstream.nextLine() + "}";
                        System.out.println("-------" + result);
                    } catch (IOException eio) {
                        eio.getStackTrace();
                        noresult = true;
                    }
                    if (noresult) {

                    } else {
                        Intent intent = new Intent(CHANNEL);
                        intent.putExtra(INFO, result);
                        intent.putExtra("numchange",numchange);
                        sendBroadcast(intent);
                    }
                }

        }
    }
    /*public JSONArray getjsonlistsity(String wordofcity) {

        PlacesQuery placesQuery=new PlacesQuery();
        placesQuery.setQuery(wordofcity);
        placesQuery.setType(PlacesQuery.Type.CITY);
        placesQuery.setHitsPerPage(10);
        String string="";
        System.out.println("--------------------------------------"+query.getApplicationID());
        //AnotherThread anotherThread=new AnotherThread(placesQuery);
        //anotherThread.run();
        try {
            AnotherThread2 anotherThread2 = new AnotherThread2(new URL("https://api.openweathermap.org/data/2.5/weather?q=" + wordofcity + "&lang=ru&appid=11380ed4b5872057ec582d1289415365"));
            anotherThread2.run();
        }catch(IOException e){

        }


        return jsonlistsity;
    }*/


   /* public class AnotherThread extends Thread{
        private PlacesQuery placesQuery;
        AnotherThread(PlacesQuery placesQuery){this.placesQuery=placesQuery;}
        @Override
        public void run() {
            synchronized (placesQuery){
                System.out.println(placesQuery);
                CompletionHandler completionHandler=new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                        System.out.println("EEEEEEEEEEEEEEror: "+e);
                        try {
                            System.out.println(jsonObject);
                            jsonlistsity = (JSONArray) jsonObject.get("cities");
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                };
                query.searchAsync(placesQuery,completionHandler);
            }

        }
    }
    */
    public class AnotherThread2 extends Thread{
        URL url;
        AnotherThread2(URL url){
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
            }

        }
    }

}
