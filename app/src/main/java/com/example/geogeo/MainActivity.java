package com.example.geogeo;


import androidx.appcompat.app.AppCompatActivity;


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
    int kolchanges;
    public static EditText editsity;
    JSONArray jsonlistcities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textdegree = (TextView) findViewById(R.id.currentdegree);
        textsky = (TextView) findViewById(R.id.sky);
        textsity = (TextView) findViewById(R.id.sity);
        Button button=(Button) findViewById(R.id.but);
        editsity=(EditText) findViewById(R.id.editsity);
        Maintext=(TextView) findViewById(R.id.text1);
        kolchanges=0;
        //Search search;
        //search = new Search();
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kolchanges++;
                String stringofword=String.valueOf(s);
                System.out.println(stringofword);
                registerReceiver(receiverlistofcities, new IntentFilter(Search.CHANNEL));
                Intent intent = new Intent(getApplication(), Search.class);
                intent.putExtra("city", stringofword);
                intent.putExtra("numchange",kolchanges);
                stopService(intent);
                startService(intent);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editsity.addTextChangedListener(textWatcher);



        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if((editsity.getText().toString()).compareTo("")!=0) {
                    String city = editsity.getText() + "";
                    registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
                    Intent intent = new Intent(getApplication(), Geoservice.class);
                    intent.putExtra("city", city);
                    startService(intent);
                    kolchanges=0;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((editsity.getText().toString()).compareTo("")!=0){
            String city =editsity.getText()+"";
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
            startService(intent);
            kolchanges=0;
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
    protected BroadcastReceiver receiverlistofcities = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (kolchanges == intent.getIntExtra("numchange", 0)) {
                try {
                    JSONArray jsonlistcities = (JSONArray) new JSONObject((intent.getStringExtra(Search.INFO))).get("list");
                    for (int i = 0; i < jsonlistcities.length(); i++) {
                        JSONObject jsonruscity = (JSONObject) jsonlistcities.get(i);
                        if (((JSONObject) jsonruscity.get("local_names")).has("ru")) {
                            System.out.println(((JSONObject) jsonruscity.get("local_names")).getString("ru") + " / " + jsonruscity.getString("country"));
                        }else{
                            System.out.println((jsonruscity.getString("name")) + " / " + jsonruscity.getString("country"));

                        }
                    }
                } catch (JSONException json) {

                }
                kolchanges=0;
            }
        }
    };
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
}