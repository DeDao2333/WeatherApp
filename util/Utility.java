package com.example.java.weatherapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.java.weatherapp.model.City;
import com.example.java.weatherapp.model.CoolWeatherDB;
import com.example.java.weatherapp.model.Country;
import com.example.java.weatherapp.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 59575 on 2016/12/18.
 */

public class Utility {
    /**
     * 解析和处理（存储）服务器返回来的数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,
                                                               String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProinces = response.split(",");
            if (allProinces != null && allProinces.length > 0) {
                for (String p : allProinces) {
                    String[] array = p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return  true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities.length > 0 && allCities != null) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setId(provinceId);
                    coolWeatherDB.saveCity(city);

                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties.length > 0 && allCounties != null) {
                for (String ac : allCounties) {
                    String[] array = ac.split("\\|");
                    Country country = new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response, TextView city_name,
                                             TextView temp1, TextView temp2, TextView weather_desp,
                                             TextView current_time,TextView publish) {
        try{
            Log.d("wer","start handle");
            Log.d("wer", "handle:  "+response);
            SimpleDateFormat adf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            city_name.setText(cityName);
//            Log.d("wer","city: "+cityName);
//            String weartherCode = weatherInfo.getString("id");
            String temp11 = weatherInfo.getString("temp1");
            temp1.setText(temp11);
            String temp22= weatherInfo.getString("temp2");
            temp2.setText(temp22);
            String weatherDesp = weatherInfo.getString("weather");
            weather_desp.setText(weatherDesp);
            String publishTime = weatherInfo.getString("ptime");
            publish.setText(publishTime);
            current_time.setText(adf.format(new Date()));
            saveWeatherInfo(context,cityName,"",temp11,temp22,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                       String temp1,String temp2,String weatherDesp,String publishTime) {
        SimpleDateFormat adf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
//        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", adf.format(new Date()));
        editor.commit();

    }
}
