package com.example.java.weatherapp.test1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.java.weatherapp.R;
import com.example.java.weatherapp.util.HttpCallbackListener;
import com.example.java.weatherapp.util.HttpUtil;
import com.example.java.weatherapp.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    private TextView current_time;
    private TextView publish_time;
    private TextView city_name;
    private TextView temp1,temp2;
    private TextView weather_desp;
    private Intent intent;
    private String weather_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        current_time = (TextView) findViewById(R.id.current_time);
        publish_time = (TextView) findViewById(R.id.publish_time);
        city_name = (TextView) findViewById(R.id.city_name);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        weather_desp = (TextView) findViewById(R.id.weather_desp);
        intent=getIntent();
        weather_code=intent.getStringExtra("weather_code");
        queryWeatherInfo(weather_code);
    }

    private void queryWeatherInfo(String weather_code){
        String address="http://www.weather.com.cn/data/cityinfo/"+"101"+weather_code+".html";
        Log.d("wer", address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.d("wer",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.handleWeatherResponse(WeatherActivity.this,response,city_name,temp1,temp2,
                                weather_desp,current_time,publish_time);
                        Init_load();
                    }
                });
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Init_load();
//                    }
//                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void Init_load(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("wer","start init_load ");
        Log.d("wer",preferences.getString("city_name",""));
//        city_name.setText(preferences.getString("city_name",""));
//        temp1.setText(preferences.getString("temp1", ""));
//        temp2.setText(preferences.getString("temp2", ""));
//        weather_desp.setText(preferences.getString("weather_desp", ""));
//        publish_time.setText(preferences.getString("publish_time", ""));
//        current_time.setText(preferences.getString("current_date", ""));
    }
}
