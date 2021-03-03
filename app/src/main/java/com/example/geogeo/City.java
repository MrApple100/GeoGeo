package com.example.geogeo;

public class City {
    private String NameCity;
    private String NameCountry;
    private String Longitude;
    private String Latitude;
    private String ID="";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public City(String nameCity, String nameCountry, String longitude, String latitude) {
        NameCity = nameCity;
        NameCountry = nameCountry;
        Longitude=longitude;
        Latitude=latitude;
    }

    public String getNameCity() {
        return NameCity;
    }

    public void setNameCity(String nameCity) {
        NameCity = nameCity;
    }

    public String getNameCountry() {
        return NameCountry;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setNameCountry(String nameCountry) {
        NameCountry = nameCountry;
    }
}
