package com.example.java.weatherapps.CoolWeather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 59575 on 2017/5/10.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String cityId;
    private String weatherId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

}
