package com.example.geogeo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @PrimaryKey
    int position;
    String When;
    String Wheather;
    String MinDegree;
    String MaxDegree;

    public Day(int position, String When, String Wheather, String MinDegree, String MaxDegree) {
        this.position = position;
        this.When = When;
        this.Wheather = Wheather;
        this.MinDegree = MinDegree;
        this.MaxDegree = MaxDegree;
    }

    public String getWhen() {
        return When;
    }

    public void setWhen(String when) {
        When = when;
    }

    public String getWheather() {
        return Wheather;
    }

    public void setWheather(String wheather) {
        Wheather = wheather;
    }

    public String getMinDegree() {
        return MinDegree;
    }

    public void setMindegree(String MinDegree) {
        MinDegree = MinDegree;
    }

    public String getMaxDegree() {
        return MaxDegree;
    }

    public void setMaxDegree(String maxDegree) {
        MaxDegree = maxDegree;
    }
}
