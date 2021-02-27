package com.example.geogeo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewSearch extends AppCompatActivity {
    int kolchanges;
    EditText editsity;
    TextView textnotfound;
    ArrayList<City> cityArrayList;
    RecyclerView searchcitylist;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editsity=(EditText) findViewById(R.id.searchword);
        textnotfound=(TextView) findViewById(R.id.notfound);
        searchcitylist=(RecyclerView) findViewById(R.id.citylist);

        kolchanges=0;
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
                textnotfound.setText("Поиск...");
                stopService(intent);
                startService(intent);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editsity.addTextChangedListener(textWatcher);
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*if((editsity.getText().toString()).compareTo("")!=0){
            String city =editsity.getText()+"";
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
            startService(intent);
            kolchanges=0;
        }*/
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

            cityArrayList=new ArrayList<City>();
            cityArrayList.clear();
            String city="",country="";
            System.out.println(kolchanges+""+intent.getIntExtra("numchange", -1));
            if (kolchanges == intent.getIntExtra("numchange", -1)) {
                try {
                    JSONArray jsonlistcities = (JSONArray) new JSONObject((intent.getStringExtra(Search.INFO))).get("list");
                    System.out.println(jsonlistcities);
                    for (int i = 0; i < jsonlistcities.length(); i++) {
                        JSONObject jsonruscity = (JSONObject) jsonlistcities.get(i);
                        if (((JSONObject) jsonruscity.get("local_names")).has("ru")) {
                            city=((JSONObject) jsonruscity.get("local_names")).getString("ru");
                        }else{
                            city=(jsonruscity.getString("name"));
                        }
                        country=jsonruscity.getString("country");
                        cityArrayList.add(new City(city,country));
                    }

                } catch (JSONException json) {

                }
            }
                kolchanges=0;
                SearchAdapter searchAdapter=null;
                if(cityArrayList.size()!=0) {
                    textnotfound.setText("");
                    searchAdapter = new SearchAdapter(ViewSearch.this, cityArrayList);
                }else
                {
                    textnotfound.setText("Ничего не найдено");
                }
                searchcitylist.setAdapter(searchAdapter);

        }
    };
    protected BroadcastReceiver receivercurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String intentstring=intent.getStringExtra(Geoservice.INFOCurrent);
                JSONObject jsonweathercurrent = new JSONObject(intentstring);
                JSONObject jsonbase =(JSONObject) jsonweathercurrent.get("gis");
                /*
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
                 */
            } catch (JSONException e) {
                e.getStackTrace();
                Toast.makeText(ViewSearch.this, "Wrong JSON format", Toast.LENGTH_LONG).show();
            }
        }
    };
}
