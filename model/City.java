package com.example.java.weatherapp.model;

/**
 * Created by 59575 on 2016/12/17.
 */

public class City {
    private int id;
    private String cityName;
    private String cityCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
