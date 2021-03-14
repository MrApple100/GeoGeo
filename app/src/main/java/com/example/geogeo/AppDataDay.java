package com.example.geogeo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Day.class}, version = 1)
public abstract class AppDataDay  extends RoomDatabase {
    private static Builder instance;
    public AppDataDay() { }
    public static  Builder getInstance(Context context, String namedatabase){
        if(instance==null) {
            instance = Room.databaseBuilder(context, AppDataDay.class, namedatabase);
        }
        return instance;
    }

    public abstract DayDao dayDao();
}

