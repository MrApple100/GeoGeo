package com.example.geogeo;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    RelativeLayout fon;
    TextView textdegree;
    TextView textsky;
    TextView textsity;
    TextView Maintext;
    int TAG_CODE_PERMISSION_LOCATION= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textdegree = (TextView) findViewById(R.id.currentdegree);
        fon =(RelativeLayout) findViewById(R.id.fon) ;
        textsky = (TextView) findViewById(R.id.sky);
        textsity = (TextView) findViewById(R.id.sity);
        Button search=(Button) findViewById(R.id.search_go_btn);
        Maintext=(TextView) findViewById(R.id.text1);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},TAG_CODE_PERMISSION_LOCATION);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ViewSearch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkk"+intent);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.newfromright,R.anim.oldtoright);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        if(receivercurrent.isOrderedBroadcast()){
            unregisterReceiver(receivercurrent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //запрос отправляется раньше основного запроса, поэтому его не видно
        if((textsity.getText().toString()).compareTo("")!=0){
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            String city =textsity.getText()+"";
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
            intent.putExtra(Geoservice.PERMISSION,"city");
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        if(receivercurrent.isOrderedBroadcast()){
            unregisterReceiver(receivercurrent);
        }
    }

    protected BroadcastReceiver receivercurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(intent.getStringExtra(Geoservice.PERMISSION).compareTo("lonlat")==0) {
                    String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                    JSONObject jsonweathercurrent = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");
                    System.out.println("WE ARE HERE");
                    JSONObject jsonbasecurrent=(JSONObject) jsonbase.get("current");
                    String wedescr = ((JSONObject) ((JSONArray) jsonbasecurrent.get("weather")).get(0)).getString("description");
                    System.out.println(wedescr);
                    int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                    int curdeg = curdegK - 273;
                    System.out.println(curdeg);
                    textdegree.setText(String.valueOf(curdeg));
                    textsky.setText(wedescr); //выводим  JSON-массив в текстовое поле
                    unregisterReceiver(receivercurrent);
                }
            } catch (JSONException e) {
                e.getStackTrace();
                Toast.makeText(MainActivity.this, "Wrong JSON format", Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
                case RESULT_OK:
                    System.out.println("GETRESULT");
                    String strlon=data.getStringExtra(ViewSearch.LONGITUDE);
                    String strlat = data.getStringExtra(ViewSearch.LATITUDE);
                    textsity.setText(data.getStringExtra("NAMECITY"));
                        registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
                        Intent intent = new Intent(getApplication(), Geoservice.class);
                        intent.putExtra("lon",strlon);
                        intent.putExtra("lat",strlat);
                        intent.putExtra(Geoservice.PERMISSION,"lonlat");
                        stopService(intent);
                        startService(intent);
                    break;
            case RESULT_CANCELED:
                    break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
            System.out.println("OUUU YEAR");
        }
    }
}