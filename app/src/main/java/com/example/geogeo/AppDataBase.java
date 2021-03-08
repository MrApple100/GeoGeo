package com.example.geogeo;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {AddedCity.class}, version = 2)
public abstract class AppDataBase  extends RoomDatabase {
    private static Builder instance;
    public AppDataBase() { }
    public static  Builder getInstance(Context context,String namedatabase){
        if(instance==null) {
            instance = Room.databaseBuilder(context, AppDataBase.class, namedatabase);
        }
        return instance;
    }

    public abstract AddedCityDao addedCityDao();
}
