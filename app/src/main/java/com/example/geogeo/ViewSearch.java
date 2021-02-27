package com.example.geogeo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
    public static String RESULTSEARCH="resultsearch";
    public static int kolchanges;
    EditText editsity;
    TextView textnotfound;
    ArrayList<City> cityArrayList=new ArrayList<City>();;
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
                cityArrayList.clear();
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

            int temptreadnum=intent.getIntExtra("numchange", -1);
            String temptreadinfo=intent.getStringExtra(Search.INFO);
            System.out.println("y"+kolchanges+"/"+temptreadnum);
            System.out.println(temptreadinfo);
            //найдены города по запросу
            if (kolchanges == temptreadnum &&(temptreadinfo.compareTo(Search.ERROR)!=0) && (temptreadinfo.compareTo("{\"list\":[]}")!=0)) {
                SearchAdapter searchAdapter=null;
                String city="",country="";
                cityArrayList.clear();
                try {
                    JSONArray jsonlistcities = (JSONArray) new JSONObject(temptreadinfo).get("list");
                    System.out.println("????????/"+jsonlistcities.length());
                    for (int i = 0; i < jsonlistcities.length(); i++) {
                        JSONObject jsonruscity = (JSONObject) jsonlistcities.get(i);
                        if(jsonruscity.has("local_names")){
                            if (((JSONObject) jsonruscity.get("local_names")).has("ru")) {
                                System.out.println("llllk");
                                city=((JSONObject) jsonruscity.get("local_names")).getString("ru");

                            }else{
                                System.out.println("kkkkkk");
                                city=(jsonruscity.getString("name"));

                            }
                        }else{
                            System.out.println("kkkkkk");
                            city=(jsonruscity.getString("name"));
                        }
                        country=jsonruscity.getString("country");
                        cityArrayList.add(new City(city,country));
                        System.out.println(i+"-00-"+cityArrayList.get(i).getNameCity());
                    }
                    System.out.println(cityArrayList.size());

                } catch (JSONException json) {

                }

                textnotfound.setText("");
                searchAdapter = new SearchAdapter(ViewSearch.this, cityArrayList);
                searchcitylist.setAdapter(searchAdapter);
                for(int i=0;i<cityArrayList.size();i++)
                    System.out.println(cityArrayList.get(i).getNameCity());
                kolchanges=0;
            }
            //не найдены города по запросу
            if (kolchanges == temptreadnum && ((temptreadinfo.compareTo(Search.ERROR)==0) || (temptreadinfo.compareTo("{\"list\":[]}")==0))) {
                    textnotfound.setText("Ничего не найдено");
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
    public void ChooseCity(View view){
        kolchanges=0;
        TextView NameCity=(TextView) view.findViewById(R.id.NameCity);
        System.out.println(NameCity.getText());
        TextView NameCountry=(TextView) view.findViewById(R.id.NameCountry);
        String result=NameCity.getText()+","+NameCountry.getText();
        Intent intent=new Intent();
        intent.putExtra(RESULTSEARCH,result);
        setResult(RESULT_OK,intent);
        finish();
    }
}
