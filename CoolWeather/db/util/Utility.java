package com.example.java.weatherapps.CoolWeather.db.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.java.weatherapps.CoolWeather.db.City;
import com.example.java.weatherapps.CoolWeather.db.County;
import com.example.java.weatherapps.CoolWeather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 59575 on 2017/5/11.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject pvObject = jsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(pvObject.getString("name"));
                    province.setProvinceCode(String.valueOf(pvObject.getInt("id")));
                    province.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, String provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject pvObject = jsonArray.getJSONObject(i);
                    City city=new City();
                    city.setCityName(pvObject.getString("name"));
                    city.setCityCode(String.valueOf(pvObject.getInt("id")));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                Log.d("wer", "3");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, String cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject pvObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(pvObject.getString("name"));
                    county.setWeatherId(pvObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @NonNull
    public static String changeListIntoString(List<String> later) {
        StringBuilder builder = new StringBuilder();
        for (String a : later) {
            builder.append(a);
        }
        return builder.toString();
    }
}
