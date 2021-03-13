package com.example.geogeo;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewSearch extends AppCompatActivity {
    private AppDataBase dataBase;
    private AddedCityDao addedCityDao;
    private AppDataMyPos datamypos;
    private MyGeoPosition myGeoPosition;
    private MyGeoPositionDao myGeoPositionDao;
    public static String LONGITUDE = "LONGITUDE";
    public static String LATITUDE = "LATITUDE";
    public static int kolchanges;
    int TAG_CODE_PERMISSION_LOCATION= 1;
    EditText editsity;
    TextView textnotfound;
    ArrayList<City> cityArrayList=new ArrayList<City>();
    ArrayList<Integer> arrayTorFadded=new ArrayList<>();
    ArrayList<City> citiesadded=new ArrayList<>();
    HashMap<Integer,String> hashmapdegree=new HashMap<>();
    RecyclerView searchcitylist;
    RecyclerView mygeoposlist;
    AddedAdapter addedAdapter;
    MyGeoPosAdapter myGeoPosAdapter;
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
    public static final Migration MIGRATIONmygeopos1_2 = new Migration(1,2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE MyGeoPosition ADD COLUMN degree STRING DEFAULT 0 not NULL");
            database.execSQL("ALTER TABLE MyGeoPosition ADD COLUMN lastjson STRING DEFAULT 0 not NULL");
        }
    };
    public static final Migration MIGRATIONmygeopos2_3 = new Migration(2,3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE MyGeoPosition ADD COLUMN lastjson STRING DEFAULT 0 not NULL");
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CREEEEEAAT");


        dataBase = (AppDataBase) AppDataBase.getInstance(this, "database").addMigrations(MIGRATION1_2).allowMainThreadQueries().build();
        addedCityDao = dataBase.addedCityDao();

        datamypos = (AppDataMyPos) AppDataMyPos.getInstance(this, "datamygeo").addMigrations(MIGRATIONmygeopos1_2,MIGRATIONmygeopos2_3).allowMainThreadQueries().build();
        myGeoPositionDao = datamypos.myGeoPositionDao();

        setContentView(R.layout.activity_search);
        editsity = (EditText) findViewById(R.id.searchword);
        textnotfound = (TextView) findViewById(R.id.notfound);
        searchcitylist = (RecyclerView) findViewById(R.id.citylist);
        mygeoposlist = (RecyclerView) findViewById(R.id.mygeoposlist);
        intentsearch = new Intent(ViewSearch.this, Search.class);
        intentgeo = new Intent(ViewSearch.this, Geoservice.class);

        kolchanges = 0;
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(s).compareTo("") != 0) {
                    //сброс удаления
                    if (AddedAdapter.getTimeforSelect()) {
                        AddedAdapter.setTimeforselect(false);
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.celldeleteadded);
                        linearLayout.setVisibility(View.GONE);
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.citylist);
                        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                            addedAdapter.setCheckarrayvisByPos(i, false);
                            addedAdapter.setCheckarraycheckByPos(i, false);
                            addedAdapter.notifyItemChanged(i);
                        }
                        Button button = (Button) findViewById(R.id.exit);
                        button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_keyboard_arrow_left_20));
                    }

                    kolchanges++;
                    String stringofword = String.valueOf(s);
                    System.out.println("++++++++++" + stringofword);
                    registerReceiver(receiverlistofcities, new IntentFilter(Search.CHANNEL));
                    intentsearch.putExtra("city", stringofword);
                    intentsearch.putExtra("numchange", kolchanges);
                    cityArrayList.clear();
                    searchcitylist.removeAllViewsInLayout();
                    searchcitylist.setVisibility(View.GONE);
                    mygeoposlist.setVisibility(View.GONE);
                    textnotfound.setText("Поиск...");
                    stopService(intentsearch);
                    startService(intentsearch);
                } else {
                    if (ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mygeoposlist.setVisibility(View.VISIBLE);
                    }
                    searchcitylist.setVisibility(View.VISIBLE);
                    kolchanges = 0;
                    textnotfound.setText("");
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                mygeoposlist.setAdapter(myGeoPosAdapter);
                            }
                                searchcitylist.setAdapter(addedAdapter);
                        }
                    };
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("ADDDD" + addedCityDao.getAll());
                            addedAdapter = new AddedAdapter(ViewSearch.this, addedCityDao.getAll());
                            if (ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(ViewSearch.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                myGeoPosAdapter = new MyGeoPosAdapter(ViewSearch.this, myGeoPositionDao.getmygeopos("mygeopos".hashCode()));

                            }
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
        editsity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //сброс удаления
                if (AddedAdapter.getTimeforSelect()) {
                    AddedAdapter.setTimeforselect(false);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.celldeleteadded);
                    linearLayout.setVisibility(View.GONE);
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.citylist);
                    for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                        addedAdapter.setCheckarrayvisByPos(i, false);
                        addedAdapter.setCheckarraycheckByPos(i, false);
                        addedAdapter.notifyItemChanged(i);
                    }
                    Button button = (Button) findViewById(R.id.exit);
                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_keyboard_arrow_left_20));
                }
                return false;
            }
        });
        //обновление местоположения и температуры
        myGeoPosAdapter = new MyGeoPosAdapter(ViewSearch.this, myGeoPositionDao.getmygeopos("mygeopos".hashCode()));
        if(addedCityDao.getAll()!=null){
            addedAdapter = new AddedAdapter(ViewSearch.this, addedCityDao.getAll());
            searchcitylist.setAdapter(addedAdapter);
            for(int i=0;i<addedCityDao.getAll().size();i++) {
                AddedCity addedCity = addedCityDao.getAll().get(i);
                registerReceiver(receivercurrentSearch, new IntentFilter(Geoservice.CHANNEL));
                Intent intent = new Intent(ViewSearch.this, Geoservice.class);
                intent.putExtra("lon", addedCity.lon);
                intent.putExtra("lat",addedCity.lat);
                intent.putExtra(Geoservice.PERMISSION, "updateadded");
                startService(intent);
            }


        }
        //проверка на то, есть ли разрешение
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (myGeoPositionDao.getmygeopos(("mygeopos").hashCode()) == null) {
                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(ViewSearch.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeoposNEW");
                        startService(intentmygeopos);
                    } else {

                    myGeoPosAdapter = new MyGeoPosAdapter(ViewSearch.this, myGeoPositionDao.getmygeopos("mygeopos".hashCode()));
                    mygeoposlist.setAdapter(myGeoPosAdapter);

                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(ViewSearch.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeopos");
                        startService(intentmygeopos);
                    }
                }
            });

        } else {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, TAG_CODE_PERMISSION_LOCATION);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        kolchanges=0;
        if(String.valueOf(editsity.getText()).compareTo("")==0) {
            textnotfound.setText("");
            searchcitylist.setVisibility(View.VISIBLE);
            mygeoposlist.setVisibility(View.VISIBLE);
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
//проверить позже
    @Override
    protected void onRestart() {
        super.onRestart();
        kolchanges=0;
        if(String.valueOf(editsity.getText()).compareTo("")!=0) {

            String stringofword = editsity.getText()+"";
            registerReceiver(receiverlistofcities, new IntentFilter(Search.CHANNEL));
            intentsearch.putExtra("city", stringofword);
            intentsearch.putExtra("numchange", kolchanges);
            cityArrayList.clear();
            searchcitylist.removeAllViewsInLayout();
            searchcitylist.setVisibility(View.GONE);
            textnotfound.setText("Поиск...");
            stopService(intentsearch);
            startService(intentsearch);
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
            LinearLayout linearLayout=(LinearLayout) findViewById(R.id.celldeleteadded);
            linearLayout.setVisibility(View.GONE);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                addedAdapter.setCheckarrayvisByPos(i,false);
                addedAdapter.setCheckarraycheckByPos(i,false);
                addedAdapter.notifyItemChanged(i);
            }
            Button button=(Button) findViewById(R.id.exit);
            button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_keyboard_arrow_left_20));
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
            LinearLayout linearLayout=(LinearLayout) findViewById(R.id.celldeleteadded);
            linearLayout.setVisibility(View.GONE);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                    addedAdapter.setCheckarrayvisByPos(i,false);
                    addedAdapter.setCheckarraycheckByPos(i,false);
                    addedAdapter.notifyItemChanged(i);
                }
            Button button=(Button) findViewById(R.id.exit);
            button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_keyboard_arrow_left_20));
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
                arrayTorFadded.clear();
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
                        int idcity = (jsonruscity.getString("lon") + jsonruscity.getString("lat")).hashCode();

                        for(int j=0;j<addedCityDao.getAll().size();j++){
                            if(addedCityDao.getAll().get(j).getId()==idcity){
                                arrayTorFadded.add(idcity);
                            }
                        }
                        cityArrayList.add(new City(city,country,longitude,latitude));
                        System.out.println(i+"-00-"+cityArrayList.get(i).getNameCity());
                    }


                } catch (JSONException json) {

                }

                textnotfound.setText("");

                for(int i=0;i<cityArrayList.size();i++){
                    registerReceiver(receivercurrentSearch, new IntentFilter(Geoservice.CHANNEL));
                    intentgeo.putExtra("lon", cityArrayList.get(i).getLongitude());
                    intentgeo.putExtra("lat", cityArrayList.get(i).getLatitude());
                    intentgeo.putExtra(Geoservice.PERMISSION,"lonlat");
                    startService(intentgeo);
                }

                searchAdapter = new SearchAdapter(ViewSearch.this, cityArrayList,arrayTorFadded);
                searchcitylist.setVisibility(View.VISIBLE);
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
            System.out.println("-----1111111-------");
            //получаем у каждого из списка degree
            if(intent.getStringExtra(Geoservice.PERMISSION).compareTo("lonlat")==0) {
                System.out.println("0909090"+intent.getStringExtra(Geoservice.INFOCurrent));
                try {
                    String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                    JSONObject jsonweathercurrent = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");

                    int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                    String strlon=intent.getStringExtra(Geoservice.GOODCOORD).split("/")[0];
                    String strlat=intent.getStringExtra(Geoservice.GOODCOORD).split("/")[1];
                    int curdeg = curdegK - 273;
                    String degree = curdeg + "";
                    System.out.println("DEGREE"+degree+"//"+strlon+"/\\"+strlat);
                    hashmapdegree.put((strlon+strlat).hashCode(),degree);
                    System.out.println((strlon+strlat).hashCode());
                    //unregisterReceiver(receivercurrentSearch);
                } catch (JSONException e) {
                    e.getStackTrace();
                    Toast.makeText(ViewSearch.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();

                }
            }else{
                //обновление моей позиции
                if(intent.getStringExtra(Geoservice.PERMISSION).compareTo("bymygeopos")==0){
                    System.out.println("070707070"+intent.getStringExtra(Geoservice.INFOCurrent));
                    try {
                        String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                        JSONObject jsonweathercurrent = new JSONObject(intentstring);
                        JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");

                        int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                        String strlon=jsonbase.getString("lon");
                        String strlat=jsonbase.getString("lat");
                        int curdeg = curdegK - 273;
                        String degree = curdeg + "";
                        myGeoPosition =new MyGeoPosition();
                        myGeoPosition.setMygeopos("mygeopos".hashCode());
                        System.out.println(strlat+"///"+strlon);
                        myGeoPosition.setLon(strlon);
                        myGeoPosition.setLat(strlat);
                        myGeoPosition.setDegree(degree);
                        myGeoPosition.setLastjson(intentstring);
                        Handler handlernew = new Handler() {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);
                                mygeoposlist.setAdapter(myGeoPosAdapter);
                            }
                        };
                        Handler handlerupdate = new Handler() {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);
                               // mygeoposlist.setAdapter(myGeoPosAdapter);
                                ((MyGeoPosAdapter.ViewHolder)mygeoposlist.getChildViewHolder(mygeoposlist.getChildAt(0))).Degree.setText(myGeoPosition.degree);
                                ((MyGeoPosAdapter.ViewHolder)mygeoposlist.getChildViewHolder(mygeoposlist.getChildAt(0))).Longitude.setText(myGeoPosition.lon);
                                ((MyGeoPosAdapter.ViewHolder)mygeoposlist.getChildViewHolder(mygeoposlist.getChildAt(0))).Latitude.setText(myGeoPosition.lat);

                                myGeoPosAdapter.notifyDataSetChanged();

                            }
                        };
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                if(intent.getStringExtra("ACTION").compareTo("new")==0){
                                    myGeoPositionDao.insert(myGeoPosition);
                                    myGeoPosAdapter =new MyGeoPosAdapter(ViewSearch.this,myGeoPositionDao.getmygeopos("mygeopos".hashCode()));
                                    handlernew.sendEmptyMessage(1);
                                }else{
                                    myGeoPositionDao.update(myGeoPosition);


                                    // myGeoPosAdapter =new MyGeoPosAdapter(ViewSearch.this,myGeoPositionDao.getmygeopos("mygeopos".hashCode()));
                                   handlerupdate.sendEmptyMessage(1);
                                }


                            }
                        });

                        unregisterReceiver(receivercurrentSearch);
                    } catch (JSONException e) {
                        e.getStackTrace();
                        Toast.makeText(ViewSearch.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();

                    }
                }else
                    //обновление addedlist
                    if(intent.getStringExtra(Geoservice.PERMISSION).compareTo("updateadded")==0){
                        System.out.println("-----------------oooooooooooooooo---------------------");
                    try {
                        String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                        JSONObject jsonweathercurrent = new JSONObject(intentstring);
                        JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");

                        int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                        String strlon=intent.getStringExtra(Geoservice.GOODCOORD).split("/")[0];
                        String strlat=intent.getStringExtra(Geoservice.GOODCOORD).split("/")[1];
                        int curdeg = curdegK - 273;
                        String degree = curdeg + "";
                        System.out.println("DEGREE"+degree+"//"+strlon+"/\\"+strlat);
                        hashmapdegree.put((strlon+strlat).hashCode(),degree);
                        String Name=addedCityDao.getByid((strlon+strlat).hashCode()).NameCity;
                        String Country=addedCityDao.getByid((strlon+strlat).hashCode()).Country;
                        String Lon=addedCityDao.getByid((strlon+strlat).hashCode()).lon;
                        String Lat=addedCityDao.getByid((strlon+strlat).hashCode()).lat;
                        AddedCity addedCity=new AddedCity(Name,Country,Lon,Lat);
                        addedCity.setDegree(degree);
                        addedCity.setId((strlon+strlat).hashCode());
                        addedCityDao.update(addedCity);

                        int temp=-1;
                        for(int i=0;i<AddedAdapter.ids.size();i++) {
                            if((strlon+strlat).compareTo(AddedAdapter.ids.get(i))==0){
                                System.out.println("pppppppppppppppp"+i);
                                temp=i;
                                ((AddedAdapter.ViewHolder) searchcitylist.getChildViewHolder(searchcitylist.getChildAt(temp))).Degree.setText(degree);
                                addedAdapter.notifyItemChanged(temp);
                            }

                        }

                       //unregisterReceiver(receivercurrentSearch);
                    } catch (JSONException e) {
                        e.getStackTrace();
                        Toast.makeText(ViewSearch.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();

                    }
                }
            }
        }
    };
    protected BroadcastReceiver receiverGeoPosition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeopos")==0 || intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeoposNEW")==0) {
                System.out.println("MYGEOPOSITIONSERVICE"+intent.getStringExtra(MyGeoPositisionService.INFOMYGEOPOSOTION));
                try {
                    String intentstring = intent.getStringExtra(MyGeoPositisionService.INFOMYGEOPOSOTION);
                    JSONObject jsonpositioninfo = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonpositioninfo.get("geoinfo");
                    String LocationGPS = jsonbase.getString("LocationGPS");
                    String LocationNet = jsonbase.getString("LocationNet");
                    String EnabledGPS = jsonbase.getString("EnabledGPS");
                    String EnabledNet = jsonbase.getString("EnabledNet");
                    String strlon="";
                    String strlat="";
                    System.out.println(LocationGPS);
                    if(LocationGPS.compareTo("")==0){
                        strlon=LocationNet.split("/")[1];
                        strlat=LocationNet.split("/")[0];
                    }else{
                        strlon=LocationGPS.split("/")[1];
                        strlat=LocationGPS.split("/")[0];
                    }
                    registerReceiver(receivercurrentSearch,new IntentFilter(Geoservice.CHANNEL));
                    intentgeo.putExtra(MyGeoPositisionService.PERMISSION,"bymygeopos");
                    if(intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeopos")==0){
                        intentgeo.putExtra("ACTION","update");
                    }else if(intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeoposNEW")==0){
                        intentgeo.putExtra("ACTION","new");
                    }
                    intentgeo.putExtra("lon",strlon);
                    intentgeo.putExtra("lat",strlat);
                    startService(intentgeo);
                    unregisterReceiver(receiverGeoPosition);
                } catch (JSONException e) {
                    e.getStackTrace();
                    Toast.makeText(ViewSearch.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    public  void ChooseCity(View view){
            kolchanges = 0;
            stopService(new Intent(ViewSearch.this, Search.class));
            stopService(new Intent(ViewSearch.this,Geoservice.class));
            TextView Longitude = (TextView) view.findViewById(R.id.longitude);
            TextView Latitude = (TextView) view.findViewById(R.id.latitude);
            TextView Namecity = (TextView) view.findViewById(R.id.NameCity);
            Intent intent = new Intent();
            intent.putExtra(LONGITUDE, String.valueOf(Longitude.getText()).split(":")[1]);
            intent.putExtra(LATITUDE,String.valueOf(Latitude.getText()).split(":")[1]);
            intent.putExtra("NAMECITY",Namecity.getText());
            setResult(RESULT_OK, intent);
            ViewSearch.this.finish();

    }
    public  void ChooseCityfromAdded(View view){
        if(!AddedAdapter.getTimeforSelect()) {
            kolchanges = 0;
            stopService(new Intent(ViewSearch.this, Search.class));
            stopService(new Intent(ViewSearch.this,Geoservice.class));
            stopService(new Intent(ViewSearch.this, MyGeoPositisionService.class));
            TextView Longitude = (TextView) view.findViewById(R.id.longitude);
            TextView Latitude = (TextView) view.findViewById(R.id.latitude);
            TextView Namecity = (TextView) view.findViewById(R.id.NameCity);
            Intent intent = new Intent();
            intent.putExtra(LONGITUDE, String.valueOf(Longitude.getText()).split(":")[1]);
            intent.putExtra(LATITUDE,String.valueOf(Latitude.getText()).split(":")[1]);
            intent.putExtra("NAMECITY",Namecity.getText());
            setResult(RESULT_OK, intent);
            ViewSearch.this.finish();
        }else {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.citylist);

            if (!view.getTag().equals(-1)) {
                if (!(addedAdapter.getCheckarraycheck().get(recyclerView.getChildLayoutPosition(view)))) {
                    System.out.println(recyclerView.getChildLayoutPosition(view));
                    addedAdapter.setCheckarraycheckByPos(recyclerView.getChildLayoutPosition(view), true);
                } else {
                    addedAdapter.setCheckarraycheckByPos(recyclerView.getChildLayoutPosition(view), false);
                }
                addedAdapter.notifyDataSetChanged();
            }
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
            int idcity = (jsoncity.getString("lon") + jsoncity.getString("lat")).hashCode();
        String degreecity=hashmapdegree.get(idcity);
            System.out.println(degreecity);
            System.out.println("2");
            System.out.println(jsoncity.getString("lon") + jsoncity.getString("lat"));

            System.out.println((jsoncity.getString("lon")+"/\\"+jsoncity.getString("lat")));
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
            LinearLayout linearLayout=(LinearLayout) findViewById(R.id.celldeleteadded);
            linearLayout.setVisibility(View.GONE);
            RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
            for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
                addedAdapter.setCheckarrayvisByPos(i,false);
                addedAdapter.setCheckarraycheckByPos(i,false);
                addedAdapter.notifyItemChanged(i);
            }
            Button button=(Button) findViewById(R.id.exit);
            button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_keyboard_arrow_left_20));

        }
    }
    public void DeleteAdded(View view){
        ArrayList<Integer> icount=new ArrayList<Integer>();
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.citylist);
        for(int i=addedAdapter.getItemCount()-1;i>=0;i--){
            if (addedAdapter.getCheckarraycheck().get(i)) {
                icount.add(i);
            }
        }
        for(int i=0;i<icount.size();i++){
            final int tempi = icount.get(i);
                System.out.println("GETCHILDAT"+recyclerView.getChildAt(tempi));
                AddedAdapter.ViewHolder childholder =(AddedAdapter.ViewHolder) recyclerView.findViewHolderForLayoutPosition(tempi);
                AddedCity addedCity=new AddedCity("","","","");

                int idcity = AddedAdapter.ids.get(tempi).hashCode();
                addedCity.setId(idcity);
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
                        System.out.println("DELELE");
                        addedCityDao.delete(addedCity);
                        addedAdapter= new AddedAdapter(ViewSearch.this, addedCityDao.getAll());
                        handler.sendEmptyMessage(1);
                    }

                });


        }
        addedAdapter.notifyDataSetChanged();
        AddedAdapter.setTimeforselect(false);
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.celldeleteadded);
        linearLayout.setVisibility(View.GONE);
        //for(int i=0;i<recyclerView.getAdapter().getItemCount();i++){
            //addedAdapter.setCheckarrayvisByPos(i,false);
            //addedAdapter.setCheckarraycheckByPos(i,false);
        //
       // }
        Button button=(Button) findViewById(R.id.exit);
        button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_keyboard_arrow_left_20));

    }
}
