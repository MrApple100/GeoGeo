package com.example.geogeo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyGeoPosition {
    @PrimaryKey
    int mygeopos;
    String degree;
    String lon;
    String lat;
    String lastjson;

    public String getLastjson() {
        return lastjson;
    }

    public void setLastjson(String lastjson) {
        this.lastjson = lastjson;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getMygeopos() {
        return mygeopos;
    }

    public void setMygeopos(int mygeopos) {
        this.mygeopos = mygeopos;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
