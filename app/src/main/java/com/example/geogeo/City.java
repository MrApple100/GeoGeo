package com.example.geogeo;

public class City {
    private String nameCity;
    private String nameCountry;
    private String longitude;
    private String latitude;
    private String ID="";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
       this.ID = ID;
   }

    public City(String nameCity, String nameCountry, String longitude, String latitude) {
        this.nameCity = nameCity;
        this.nameCountry = nameCountry;
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public String getNameCountry() {
        return nameCountry;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setNameCountry(String nameCountry) {
        this.nameCountry = nameCountry;
    }
}
