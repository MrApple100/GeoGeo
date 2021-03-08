package com.example.geogeo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewSearch extends AppCompatActivity {
    private AppDataBase dataBase;
    private AddedCityDao addedCityDao;
    public static String RESULTSEARCH="resultsearch";
    public static int kolchanges;
    EditText editsity;
    TextView textnotfound;
    ArrayList<City> cityArrayList=new ArrayList<City>();;
    ArrayList<City> citiesadded=new ArrayList<>();
    ArrayList<Integer> arrayid=new ArrayList<>();
    ArrayList<String> arraydegree=new ArrayList<>();
    RecyclerView searchcitylist;
    AddedAdapter addedAdapter;
    Intent intentsearch;
    Intent intentgeo;

    public AppDataBase getDataBase() {
        return dataBase;
    }
    public static final Migration MIGRATION1_2 = new Migration(1,2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE AddedCity ADD COLUMN degree STRING DEFAULT 0 not NULL");
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CREEEEEAAT");


        dataBase =(AppDataBase) AppDataBase.getInstance(this,"database").addMigrations(MIGRATION1_2).allowMainThreadQueries().build();
        addedCityDao=dataBase.addedCityDao();

        setContentView(R.layout.activity_search);
        editsity=(EditText) findViewById(R.id.searchword);
        textnotfound=(TextView) findViewById(R.id.notfound);
        searchcitylist=(RecyclerView) findViewById(R.id.citylist);
        intentsearch = new Intent(ViewSearch.this, Search.class);
        intentgeo = new Intent(ViewSearch.this, Geoservice.class);

        kolchanges=0;
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(String.valueOf(s).compareTo("")!=0) {
                    kolchanges++;
                    String stringofword = String.valueOf(s);
                    System.out.println("++++++++++" + stringofword);
                    registerReceiver(receiverlistofcities, new IntentFilter(Search.CHANNEL));
                    intentsearch.putExtra("city", stringofword);
                    intentsearch.putExtra("numchange", kolchanges);
                    cityArrayList.clear();
                    searchcitylist.removeAllViewsInLayout();
                    textnotfound.setText("Поиск...");
                    stopService(intentsearch);
                    startService(intentsearch);
                }else{
                    kolchanges=0;
                    textnotfound.setText("");
                    Handler handler=new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            searchcitylist.setAdapter(addedAdapter);
                        }
                    };
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("ADDDD"+addedCityDao.getAll());
                            addedAdapter= new AddedAdapter(ViewSearch.this, addedCityDao.getAll());
                            handler.sendEmptyMessage(1);
                        }
                    });

                }
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
        kolchanges=0;
        if(String.valueOf(editsity.getText()).compareTo("")==0) {
            textnotfound.setText("");
            Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    searchcitylist.setAdapter(addedAdapter);
                }
            };
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ADDDD" + addedCityDao.getAll());
                    addedAdapter = new AddedAdapter(ViewSearch.this, addedCityDao.getAll());
                    handler.sendEmptyMessage(1);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        System.out.println("DESTRROOOY");
        //удаляем лишний получатель
        if(receivercurrentSearch.isOrderedBroadcast()){
            unregisterReceiver(receivercurrentSearch);
        }
        if(AddedAdapter.getTimeforSelect()){
            AddedAdapter.setTimeforselect(false);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                addedAdapter.setCheckarrayvisByPos(i,false);
                addedAdapter.setCheckarraycheckByPos(i,false);
                addedAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        System.out.println("DESTRROOOY");
        //удаляем лишний получатель
        if(receivercurrentSearch.isOrderedBroadcast()){
            unregisterReceiver(receivercurrentSearch);
        }
        //возвращаем в стандартное состояние addedarray
        if(AddedAdapter.getTimeforSelect()){
            AddedAdapter.setTimeforselect(false);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                    addedAdapter.setCheckarrayvisByPos(i,false);
                    addedAdapter.setCheckarraycheckByPos(i,false);
                    addedAdapter.notifyItemChanged(i);
                }
        }

    }
    protected BroadcastReceiver receiverlistofcities = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int temptreadnum=intent.getIntExtra("numchange", -1);
            String temptreadinfo=intent.getStringExtra(Search.INFO);
            //найдены города по запросу

            if (kolchanges == temptreadnum &&(temptreadinfo.compareTo(Search.ERROR)!=0) && (temptreadinfo.compareTo("{\"list\":[]}")!=0)) {
                SearchAdapter searchAdapter=null;
                String city="",country="",longitude="",latitude="";
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
                        longitude=jsonruscity.getString("lon");
                        latitude=jsonruscity.getString("lat");
                        cityArrayList.add(new City(city,country,longitude,latitude));
                        System.out.println(i+"-00-"+cityArrayList.get(i).getNameCity());
                    }
                    System.out.println(cityArrayList.size());

                } catch (JSONException json) {

                }

                textnotfound.setText("");

                for(int i=0;i<cityArrayList.size();i++){
                    registerReceiver(receivercurrentSearch, new IntentFilter(Geoservice.CHANNEL));
                    intentgeo.putExtra("lon", cityArrayList.get(i).getLongitude());
                    intentgeo.putExtra("lat", cityArrayList.get(i).getLatitude());
                    intentgeo.putExtra(Geoservice.PERMISSION,"lonlat");
                    stopService(intentgeo);
                    startService(intentgeo);
                }


                searchAdapter = new SearchAdapter(ViewSearch.this, cityArrayList);
                searchcitylist.setAdapter(searchAdapter);
                for(int i=0;i<cityArrayList.size();i++)
                    System.out.println(cityArrayList.get(i).getNameCity());
                kolchanges=0;
                unregisterReceiver(receiverlistofcities);
            }
            //не найдены города по запросу
            if (kolchanges == temptreadnum && ((temptreadinfo.compareTo(Search.ERROR)==0) || (temptreadinfo.compareTo("{\"list\":[]}")==0))) {
                    textnotfound.setText("Ничего не найдено");
                kolchanges=0;
                unregisterReceiver(receiverlistofcities);
            }

        }
    };
    protected BroadcastReceiver receivercurrentSearch = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(Geoservice.PERMISSION).compareTo("id")==0) {
                System.out.println("0909090"+intent.getStringExtra(Geoservice.INFOCurrent));
                try {
                    String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                    JSONObject jsonweathercurrent = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");

                    int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                    int curdeg = curdegK - 273;
                    String degree = curdeg + "";
                    arraydegree.add(degree);
                    unregisterReceiver(receivercurrentSearch);
                } catch (JSONException e) {
                    e.getStackTrace();
                    Toast.makeText(ViewSearch.this, "Wrong JSON format", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    public  void ChooseCity(View view){
            kolchanges = 0;
            stopService(new Intent(ViewSearch.this, Search.class));
            stopService(new Intent(ViewSearch.this,Geoservice.class));
            TextView NameCity = (TextView) view.findViewById(R.id.NameCity);
            System.out.println(NameCity.getText());
            TextView NameCountry = (TextView) view.findViewById(R.id.NameCountry);
            String result = NameCity.getText() + "," + NameCountry.getText();
            Intent intent = new Intent();
            intent.putExtra(RESULTSEARCH, result);
            setResult(RESULT_OK, intent);
            ViewSearch.this.finish();

    }
    public  void ChooseCityfromAdded(View view){
        if(!AddedAdapter.getTimeforSelect()) {
            kolchanges = 0;
            stopService(new Intent(ViewSearch.this, Search.class));
            stopService(new Intent(ViewSearch.this,Geoservice.class));
            TextView NameCity = (TextView) view.findViewById(R.id.NameCity);
            System.out.println(NameCity.getText());
            TextView NameCountry = (TextView) view.findViewById(R.id.NameCountry);
            String result = NameCity.getText() + "," + NameCountry.getText();
            Intent intent = new Intent();
            intent.putExtra(RESULTSEARCH, result);
            setResult(RESULT_OK, intent);
            ViewSearch.this.finish();
        }else{
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
                if(!(addedAdapter.getCheckarraycheck().get(recyclerView.getChildAdapterPosition(view)))) {
                    addedAdapter.setCheckarraycheckByPos(recyclerView.getChildAdapterPosition(view),true);
                }else{
                    addedAdapter.setCheckarraycheckByPos(recyclerView.getChildAdapterPosition(view),false);
                }
            addedAdapter.notifyItemChanged(recyclerView.getChildAdapterPosition(view));
        }

    }
    public void AddedCity(View view){
        TextView Addbut=(TextView) view;
        System.out.println("0000"+view.getTag());
        //делай галочку
        Addbut.setBackground(ContextCompat.getDrawable(this,R.drawable.nullbackground));
        Addbut.setClickable(false);
        try{
            System.out.println(Addbut.getTag());
        JSONObject jsoncity=(JSONObject) new JSONObject((String)Addbut.getTag()).get("coord");
        int id=Integer.parseInt(jsoncity.getString("idtag"));
            System.out.println("1");
        String degreecity=arraydegree.get(id);
            System.out.println("2");
            int idcity = (jsoncity.getString("lon") + jsoncity.getString("lat")).hashCode();

            System.out.println("access"+idcity);
        citiesadded.add(new City(jsoncity.getString("name"),jsoncity.getString("country"),jsoncity.getString("lon"),jsoncity.getString("lat")));
        AddedCity addedCity=new AddedCity(jsoncity.getString("name"),jsoncity.getString("country"),jsoncity.getString("lon"),jsoncity.getString("lat"));
        addedCity.setDegree(degreecity);
        addedCity.setId(idcity);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ADD");
                    addedCityDao.insert(addedCity);
                }
            });

        }catch (JSONException e){
            System.out.println("you are a bad programmer");
        }
    }
    public void Back(View view){
        EditText searchline=(EditText) findViewById(R.id.searchword);
        if(!AddedAdapter.getTimeforSelect()) {
            if ((searchline.getText() + "").compareTo("") != 0) {
                searchline.setText("");
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                ViewSearch.this.finish();
            }
        }else{
            AddedAdapter.setTimeforselect(false);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                addedAdapter.setCheckarrayvisByPos(i,false);
                addedAdapter.setCheckarraycheckByPos(i,false);
                addedAdapter.notifyItemChanged(i);
            }
        }
    }
}
