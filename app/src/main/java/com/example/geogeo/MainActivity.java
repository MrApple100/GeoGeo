package com.example.geogeo;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {

    TextView textdegree;
    TextView textsky;
    TextView textsity;
    TextView Maintext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textdegree = (TextView) findViewById(R.id.currentdegree);
        textsky = (TextView) findViewById(R.id.sky);
        textsity = (TextView) findViewById(R.id.sity);
        Button search=(Button) findViewById(R.id.search_go_btn);
        Maintext=(TextView) findViewById(R.id.text1);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ViewSearch.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((textsity.getText().toString()).compareTo("")!=0){
            String city =textsity.getText()+"";
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
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
    }

    protected BroadcastReceiver receivercurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String intentstring=intent.getStringExtra(Geoservice.INFOCurrent);
                JSONObject jsonweathercurrent = new JSONObject(intentstring);
                JSONObject jsonbase =(JSONObject) jsonweathercurrent.get("gis");

                String sity= jsonbase.getString("name");
                System.out.println(sity);
                String wedescr=((JSONObject)((JSONArray) jsonbase.get("weather")).get(0)).getString("description");
                System.out.println(wedescr);
                int curdegK=((JSONObject) jsonbase.get("main")).getInt("temp");
                int curdeg=curdegK-273;
                System.out.println(curdeg);
                textsity.setText(sity);
                textdegree.setText(String.valueOf(curdeg));
                textsky.setText(wedescr); //выводим  JSON-массив в текстовое поле
            } catch (JSONException e) {
                e.getStackTrace();
                Toast.makeText(MainActivity.this, "Wrong JSON format", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String NameCity=data.getStringExtra(ViewSearch.RESULTSEARCH);
        if(NameCity.compareTo("")!=0){
            String city =NameCity;
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
            startService(intent);
        }
    }
}