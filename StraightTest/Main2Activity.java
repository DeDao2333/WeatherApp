package com.example.java.weatherapp.StraightTest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.java.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 天气情况显示页面
 */
public class Main2Activity extends AppCompatActivity {
    private Button send_code;
    private EditText weather_code;
    private TextView disp_json,disp_city,temp_1,temp_2,weather_1;
    private int DISP_JSON=1;
    private int SETTEXT=2;
    public Context context;
    private WeatherSaveHelper weatherSaveHelper;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SetText(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context=getApplicationContext();
        weatherSaveHelper = new WeatherSaveHelper(context, "weatherinfo.db", null, 1);
        send_code = (Button) findViewById(R.id.send_code);
        weather_code = (EditText) findViewById(R.id.weather_code1);
        disp_json = (TextView) findViewById(R.id.disp_json);
        disp_city = (TextView)findViewById(R.id.disp_city);
        weather_1 = (TextView) findViewById(R.id.weather_1);
        temp_1 = (TextView) findViewById(R.id.temp_1);
        temp_2 = (TextView) findViewById(R.id.temp_2);
        Intent intent=getIntent();
        Log.d("wer", "MainActivity.class");
        Log.d("wer","MainActivity.class: "+intent.getStringExtra("county"));
        weather_code.setText(""+101+intent.getStringExtra("county"));
        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wer", "click");
                search(weather_code.getText().toString());
            }
        });

    }

    /**
     * 将城市代码换成地址
     * @param cityid 城市的代码
     * @return
     */
    public String MixTheAddress(String cityid) {
        return "http://www.weather.com.cn/data/cityinfo/"+cityid+".html";
    }
    public void SendRequest(final String cityid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(MixTheAddress(cityid));
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    //显示返回的JSON
                    Message message = new Message();
                    message.obj=response;
                    message.what=1;
                    handler.sendMessage(message);
                    //显示JSON里保存的数据
                    handleResponse(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 处理得到的 JSON 数据，并发送 message进行 UI 的更新
     * @param response JSON
     * @return
     */
    public void handleResponse(String response) {
        String cityName=null;
        String weather=null;
        String temp1=null;
        String temp2=null;
        String city_id=null;
        String ptime=null;
        try {
            JSONObject weatherinfo = new JSONObject(response).getJSONObject("weatherinfo");
            cityName = weatherinfo.getString("city");
            weather = weatherinfo.getString("weather");
            temp1 = weatherinfo.getString("temp1");
            temp2 = weatherinfo.getString("temp2");
            city_id = weatherinfo.getString("cityid");
            ptime = weatherinfo.getString("ptime");

            String[] info=new String[]{cityName,weather,temp1,temp2,city_id,ptime};

            saveDataIntoBase(info);         //保存到数据库

            Message message = new Message();
            message.obj=info;
            message.what=SETTEXT;
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储数据到Share
     * @param info city,w,t1,t2
     */
    public void saveDataIntoBase(String[] info) {
        SQLiteDatabase db=weatherSaveHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city", info[0]);
        values.put("weather", info[1]);
        values.put("temp1", info[2]);
        values.put("temp2", info[3]);
        values.put("cityid", info[4]);
        values.put("ptime", info[5]);
        db.insert("weather_info",null,values);
    }

    public void search(String city_id1) {
        WeatherInfo weatherInfo=new WeatherInfo();
        SQLiteDatabase db=weatherSaveHelper.getReadableDatabase();
        Cursor cursor = db.query("weather_info", null, "cityid=?", new String[]{city_id1}, null, null, null);
        if (cursor.moveToFirst()) {
            weatherInfo.setCityName(cursor.getString(cursor.getColumnIndex("city")));
            weatherInfo.setCity_id(cursor.getString(cursor.getColumnIndex("cityid")));
            weatherInfo.setTemp1(cursor.getString(cursor.getColumnIndex("temp1")));
            weatherInfo.setTemp2(cursor.getString(cursor.getColumnIndex("temp2")));
            weatherInfo.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
            weatherInfo.setPtime(cursor.getString(cursor.getColumnIndex("ptime")));
            Toast.makeText(context, "从数据库中找到", Toast.LENGTH_SHORT).show();
            Message message=new Message();
            message.what=SETTEXT;
            message.obj = new String[]{weatherInfo.cityName, weatherInfo.getWeather(),
                    weatherInfo.getTemp1(), weatherInfo.getTemp2()};
            handler.sendMessage(message);
        }else{
            Toast.makeText(context, "数据库中没有找到", Toast.LENGTH_SHORT).show();
            SendRequest(city_id1);
        }
    }

    public void loadDataFromShare() {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String[] info = new String[]{
                preferences.getString("city_name", "没有数据"),
                preferences.getString("weather", "没有数据"),
                preferences.getString("temp1","没有数据"),
                preferences.getString("temp2","没有数据")};
        Message message=new Message();
        message.obj=info;
        message.what=3;
        handler.sendMessage(message);
    }

    public void SetText(Message msg) {
        if (msg.what == DISP_JSON) {
            disp_json.setText(msg.obj.toString());
        }
        if (msg.what == SETTEXT) {
            String[] info= (String[])msg.obj;
            disp_city.setText(info[0]);
            weather_1.setText(info[1]);
            temp_1.setText(info[2]);
            temp_2.setText(info[3]);
        }
    }

}
