package com.example.geogeo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

    @Database(entities = {MyGeoPosition.class}, version = 2)
    public abstract class AppDataMyPos  extends RoomDatabase {
        private static Builder instance;
        public AppDataMyPos() { }
        public static  Builder getInstance(Context context, String namedatabase){
            if(instance==null) {
                instance = Room.databaseBuilder(context, AppDataMyPos.class, namedatabase);
            }
            return instance;
        }

        public abstract MyGeoPositionDao myGeoPositionDao();
    }

