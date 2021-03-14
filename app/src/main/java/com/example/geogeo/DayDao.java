package com.example.geogeo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.geogeo.Day;

import java.util.List;

@Dao
public interface DayDao {
    @Query("SELECT * FROM Day")
    List<Day> getAll();

    @Query("SELECT *FROM Day WHERE position = :position")
    Day getByid(int position);

    @Insert
    void insert(Day day);
    @Update
    void update(Day day);
    @Delete
    void delete(Day day);
}