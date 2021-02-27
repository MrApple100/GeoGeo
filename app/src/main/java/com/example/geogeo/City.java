package com.example.geogeo;

public class City {
    private String NameCity;
    private String NameCountry;

    public City(String nameCity, String nameCountry) {
        NameCity = nameCity;
        NameCountry = nameCountry;
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

    public void setNameCountry(String nameCountry) {
        NameCountry = nameCountry;
    }
}
