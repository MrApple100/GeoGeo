package com.example.geogeo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MyGeoPositionDao {

    @Query("SELECT *FROM MyGeoPosition WHERE mygeopos = :mygeopos")
    MyGeoPosition getmygeopos(int mygeopos);

    @Insert
    void insert(MyGeoPosition myGeoPosition);
    @Update
    void update(MyGeoPosition myGeoPosition);
    @Delete
    void delete(MyGeoPosition myGeoPosition);
}
