package com.example.geogeo;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    RelativeLayout fon;
    TextView textdegree;
    TextView textsky;
    TextView textsity;
    RecyclerView dayslist;
    DayAdapter dayAdapter;
    String lonlat;
    int TAG_CODE_PERMISSION_LOCATION= 1;
    private MyGeoPositionDao myGeoPositionDao;
    private AppDataMyPos datamypos;
    private AppDataDay dataDay;
    private DayDao dayDao;
    private MyGeoPosition myGeoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //инициализация виджетов
        textdegree = (TextView) findViewById(R.id.currentdegree);
        fon =(RelativeLayout) findViewById(R.id.fon) ;
        textsky = (TextView) findViewById(R.id.sky);
        textsity = (TextView) findViewById(R.id.sity);
        Button search=(Button) findViewById(R.id.search_go_btn);
        Button gotomaps=(Button) findViewById(R.id.gotomaps);
        dayslist=(RecyclerView) findViewById(R.id.dayslist);
        //добавление баз данных
        datamypos = (AppDataMyPos) AppDataMyPos.getInstance(this, "datamygeo").addMigrations(ViewSearch.MIGRATIONmygeopos1_2,ViewSearch.MIGRATIONmygeopos2_3).allowMainThreadQueries().build();
        myGeoPositionDao = datamypos.myGeoPositionDao();
        dataDay = (AppDataDay) AppDataDay.getInstance(this,"dataday").build();
        dayDao = dataDay.dayDao();
        //проверка на доступ к метонахождению
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            textsity.setText("Погода вокруг");
            if (myGeoPositionDao.getmygeopos(("mygeopos").hashCode()) != null) {
                //обновление экрана
                UpdateMain();
            }
            //работа с сервисом местонахождения
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (myGeoPositionDao.getmygeopos(("mygeopos").hashCode()) == null) {
                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(MainActivity.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeoposNEW");
                        startService(intentmygeopos);
                    } else {
                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(MainActivity.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeopos");
                        startService(intentmygeopos);
                    }
                }
            });
            //если не дали разрешение
        }else{
            textsity.setText("Москва");
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("lon","37.6156");
            intent.putExtra("lat","55.7522");
            intent.putExtra(Geoservice.PERMISSION,"lonlat");
            stopService(intent);
            startService(intent);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},TAG_CODE_PERMISSION_LOCATION);
        }
        //создает список погоды на пять дней
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                dayslist.setAdapter(dayAdapter);
            }
        };
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if(dayDao.getAll()!=null) {
                    dayAdapter = new DayAdapter(MainActivity.this, dayDao.getAll());
                    handler.sendEmptyMessage(1);
                }
            }
        });
        //переход на страницу управление городами
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
        //отправляет запрос для показа города на карте
        gotomaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+lonlat+"?zoom:15"));
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //на всякий случай
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        //ну на крайняк
        if(receivercurrent.isOrderedBroadcast()){
            unregisterReceiver(receivercurrent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //запрос отправляется раньше основного запроса, поэтому его не видно
        /*if((textsity.getText().toString()).compareTo("")!=0){
            registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
            String city =textsity.getText()+"";
            Intent intent = new Intent(getApplication(), Geoservice.class);
            intent.putExtra("city",city);
            intent.putExtra(Geoservice.PERMISSION,"city");
            startService(intent);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //если шо, перестраховка
        Intent intentweather = new Intent(this, Geoservice.class);
        stopService(intentweather);
        Intent intentsearch = new Intent(this, Search.class);
        stopService(intentsearch);
        if(receivercurrent.isOrderedBroadcast()){
            unregisterReceiver(receivercurrent);
        }
        //закрыть базы данных
        System.out.println("DESTROYMAIN");
        //dataDay.close();
        //datamypos.close();
    }
    //получатель, работает с Geoservice
    //Geoservice в завсимости от параметра Permission выполняет дейтсвие и
    //отправляет json сюда, в отдельно выделенное место для обработки
    protected BroadcastReceiver receivercurrent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getStringExtra(Geoservice.PERMISSION).compareTo("lonlat") == 0) {
                    String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                    JSONObject jsonweathercurrent = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");
                    JSONObject jsonbasecurrent = (JSONObject) jsonbase.get("current");
                    String wedescr = ((JSONObject) ((JSONArray) jsonbasecurrent.get("weather")).get(0)).getString("description");
                    JSONArray jsondays=(JSONArray) jsonbase.get("daily");
                    int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                    int curdeg = curdegK - 273;
                    //меняю цвет в зависимости от температуры
                    if(curdeg>4){
                        if(curdeg>15){
                            fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidewarm));
                        }else{
                            fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidehalfsun));
                        }
                    }else{
                        if(curdeg<-15){
                            fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidesnow));
                        }else{
                            if(curdeg<-4) {
                                fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidesnow));
                            }else{
                                fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidehalfsun));
                            }
                        }
                    }
                    lonlat=intent.getStringExtra(Geoservice.GOODCOORD).split("/")[1]+","+intent.getStringExtra(Geoservice.GOODCOORD).split("/")[0];
                    textdegree.setText(String.valueOf(curdeg));
                    textsky.setText(wedescr);
                    String currentdt= jsonbasecurrent.getString("dt"); //время отправки запроса. не пригодилось но пусть будет
                    //заполняет ячейку дня информацией
                    for(int i=0;i<jsondays.length();i++){
                        String date=((JSONObject)jsondays.get(i)).getString("dt");
                        int mindegree=((JSONObject)((JSONObject) jsondays.get(i)).get("temp")).getInt("min")-273;
                        int maxdegree=((JSONObject)((JSONObject) jsondays.get(i)).get("temp")).getInt("max")-273;
                        String weather=((JSONObject)((JSONArray)((JSONObject) jsondays.get(i)).get("weather")).get(0)).getString("description");
                        Day day=new Day(i,date,weather,mindegree+"",maxdegree+"");
                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);
                                dayslist.setAdapter(dayAdapter);

                            }
                        };
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                if(dayDao.getByid(day.position)==null){
                                    dayDao.insert(day);

                                    dayAdapter=new DayAdapter(MainActivity.this,dayDao.getAll());
                                    handler.sendEmptyMessage(1);
                                }else{
                                    dayDao.update(day);
                                    dayAdapter=new DayAdapter(MainActivity.this,dayDao.getAll());
                                    handler.sendEmptyMessage(1);
                                }
                            }
                        });
                    }
                    //разрегиваем получатель после выполненных действий, чтобы они не накапливались
                    unregisterReceiver(receivercurrent);
                } else if (intent.getStringExtra(Geoservice.PERMISSION).compareTo("bymygeopos") == 0) {
                    String intentstring = intent.getStringExtra(Geoservice.INFOCurrent);
                    JSONObject jsonweathercurrent = new JSONObject(intentstring);
                    JSONObject jsonbase = (JSONObject) jsonweathercurrent.get("gis");
                    int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
                    String strlon = jsonbase.getString("lon");
                    String strlat = jsonbase.getString("lat");
                    int curdeg = curdegK - 273;
                    String degree = curdeg + "";
                    //обновление данных о местонахождении в бд
                    myGeoPosition = new MyGeoPosition();
                    myGeoPosition.setMygeopos("mygeopos".hashCode());
                    myGeoPosition.setLon(strlon);
                    myGeoPosition.setLat(strlat);
                    myGeoPosition.setDegree(degree);
                    myGeoPosition.setLastjson(intent.getStringExtra(Geoservice.INFOCurrent));

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (intent.getStringExtra("ACTION").compareTo("new") == 0) {
                                myGeoPositionDao.insert(myGeoPosition);

                            } else {
                                myGeoPositionDao.update(myGeoPosition);
                            }
                        }
                    });
                    unregisterReceiver(receivercurrent);
                    registerReceiver(receivercurrent, new IntentFilter(Geoservice.CHANNEL));
                    Intent intentonmain = new Intent(getApplication(), Geoservice.class);
                    intentonmain.putExtra("lon",strlon);
                    intentonmain.putExtra("lat",strlat);
                    intentonmain.putExtra(Geoservice.PERMISSION,"lonlat");
                    stopService(intentonmain);
                    startService(intentonmain);

                }
            }catch (JSONException e) {
                e.getStackTrace();
                Toast.makeText(MainActivity.this,"Проверьте связь с интернетом!",Toast.LENGTH_LONG).show();
            }

        }
    };
    //получатель работает с сервисом местонахождения
    protected BroadcastReceiver receiverGeoPosition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeopos")==0 || intent.getStringExtra(MyGeoPositisionService.PERMISSION).compareTo("mygeoposNEW")==0) {
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
                    if(LocationGPS.compareTo("")==0){
                        strlon=LocationNet.split("/")[1];
                        strlat=LocationNet.split("/")[0];
                    }else{
                        strlon=LocationGPS.split("/")[1];
                        strlat=LocationGPS.split("/")[0];
                    }
                    registerReceiver(receivercurrent,new IntentFilter(Geoservice.CHANNEL));
                    Intent intentgeo = new Intent(getApplication(), Geoservice.class);
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
                    Toast.makeText(MainActivity.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    //получаем результат со страницы управления городами
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            //если получили данные
                case RESULT_OK:
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
                    //просто вышли
            case RESULT_CANCELED:
                    break;
        }
    }
    //получаем ответ на запрос разрешения для работы с местонахождением
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            textsity.setText("Погода вокруг");
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (myGeoPositionDao.getmygeopos(("mygeopos").hashCode()) == null) {
                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(MainActivity.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeoposNEW");
                        startService(intentmygeopos);
                    } else {
                    /*
                    myGeoPosAdapter = new MyGeoPosAdapter(ViewSearch.this, myGeoPositionDao.getmygeopos("mygeopos".hashCode()));
                    mygeoposlist.setAdapter(myGeoPosAdapter);
                    */
                        registerReceiver(receiverGeoPosition, new IntentFilter(MyGeoPositisionService.CHANNEL));
                        Intent intentmygeopos = new Intent(MainActivity.this, MyGeoPositisionService.class);
                        intentmygeopos.putExtra(MyGeoPositisionService.PERMISSION, "mygeopos");
                        startService(intentmygeopos);
                    }
                }
            });
        }else{

        }
    }
    //обновляем гланый экран
    public void UpdateMain(){
        try {
            JSONObject jsonObject = new JSONObject(myGeoPositionDao.getmygeopos("mygeopos".hashCode()).getLastjson());
            JSONObject jsonbase = (JSONObject) jsonObject.get("gis");
            JSONObject jsonbasecurrent = (JSONObject) jsonbase.get("current");
            String wedescr = ((JSONObject) ((JSONArray) jsonbasecurrent.get("weather")).get(0)).getString("description");
            int curdegK = ((JSONObject) jsonbase.get("current")).getInt("temp");
            int curdeg = curdegK - 273;
            //меняю цвет в зависимости от температуры
            if(curdeg>4){
                if(curdeg>15){
                    fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidewarm));
                }else{
                    fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidehalfsun));
                }
            }else{
                if(curdeg<-15){
                    fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidesnow));
                }else{
                    if(curdeg<-4) {
                        fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidesnow));
                    }else{
                        fon.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sidehalfsun));
                    }
                }
            }
            textdegree.setText(String.valueOf(curdeg));
            textsky.setText(wedescr);
        }catch(JSONException e){
                Toast.makeText(MainActivity.this,"Проверьте связь с интернетом",Toast.LENGTH_LONG).show();
        }

    }
}