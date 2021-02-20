package com.example.geogeo;


import androidx.appcompat.app.AppCompatActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    TextView textdegree;
    TextView textsky;
    TextView textsity;
    EditText editsity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textdegree = (TextView) findViewById(R.id.currentdegree);
        textsky = (TextView) findViewById(R.id.sky);
        textsity = (TextView) findViewById(R.id.sity);
        Button button=(Button) findViewById(R.id.but);
        editsity=(EditText) findViewById(R.id.editsity);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if((editsity.getText().toString()).compareTo("")!=0) {
                    String sity = editsity.getText() + "";
                    registerReceiver(receiver, new IntentFilter(Geoservice.CHANNEL));
                    Intent intent = new Intent(getApplication(), Geoservice.class);
                    intent.putExtra("sity", sity);
                    startService(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((editsity.getText().toString()).compareTo("")!=0){
            String sity =editsity.getText()+"";
            registerReceiver(receiver, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("sity",sity);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, Geoservice.class);
        stopService(intent);
    }
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject jsonweathercurrent = new JSONObject(intent.getStringExtra(Geoservice.INFOCurrent)); //получаем JSON из intent-а
                JSONObject jsonbase =(JSONObject) jsonweathercurrent.get("gis");
                String sity= jsonbase.getString("name");
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