package com.example.geogeo;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AddedCityDao {
    @Query("SELECT * FROM AddedCity")
    List<AddedCity> getAll();

    @Query("SELECT *FROM AddedCity WHERE id = :id")
    AddedCity getByid(int id);

    @Insert
    void insert(AddedCity city);
    @Update
    void update(AddedCity city);
    @Delete
    void delete(AddedCity city);
}
