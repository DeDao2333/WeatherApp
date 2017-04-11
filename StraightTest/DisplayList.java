
package com.example.java.weatherapp.StraightTest;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.java.weatherapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 城市列表显示活动页面
 */
public class DisplayList extends AppCompatActivity {
    private ListView listView;
    private List<String> listInfo = new ArrayList<>();
    private List<String> listid = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private String select_province;
    private String select_city;

    private Context context;
    private WeatherSaveHelper weatherHelper;
    private SQLiteDatabase db;

    private int current_level=0;
    private int PROVINCE=1;
    private int CITY=2;
    private int COUNTY=3;

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.DownloadBinder binder = (MyService.DownloadBinder) service;
            binder.getProgress();
            binder.startDownload();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    Intent intent = new Intent(this, MyService.class);

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("wer", "4");
            if (msg.what == 0) {
                String[] str=(String[])msg.obj;
                String response = str[0];
                handleTheMessageForProvince(response);
                adapter.notifyDataSetChanged();
                current_level=PROVINCE;
            } else if (msg.what == PROVINCE) {
                String[] str=(String[])msg.obj;
                handleTheMessageForCity(str[0],str[1]);
                adapter.notifyDataSetChanged();
                current_level = CITY;
            } else if (msg.what == CITY) {
                String[] str=(String[])msg.obj;
                handleTheMessageForCounty(str[0],str[1]);
                adapter.notifyDataSetChanged();
                current_level=COUNTY;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        weatherHelper=new WeatherSaveHelper(this,"weather1.db",null,1);
        listView = (ListView) findViewById(R.id.Main2_List);
        context=getApplicationContext();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listInfo);
        listView.setAdapter(adapter);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp_id=handlePosition(position);
                Log.d("wer", "1");
                Log.d("wer", "current: " + current_level);
                if (current_level == PROVINCE) {
                    Log.d("pro", "3");
                    select_province=temp_id;
                    Load_city(temp_id);
                } else if (current_level == CITY) {
                    Log.d("pro", "8");
                    select_city=temp_id;
                    Load_county(temp_id);
                } else if (current_level == COUNTY) {
                    Log.d("wer", "click the county");
                    Log.d("pro", "11");
                    Load_WeatherInfo(temp_id);
                    listid.remove(listid.size() - 1);
                }
                Log.d("wer", "5");
            }
        });
        Init_Load();
    }

    @Override
    public void onBackPressed() {
        if (current_level == PROVINCE) {
            finish();
        } else if (current_level == CITY) {
            listid.remove(listid.size() - 1);
            Init_Load();
            current_level = PROVINCE;
        } else if (current_level == COUNTY) {
            listid.remove(listid.size() - 1);
            Load_city(select_province);
            current_level = CITY;
        } else {
            finish();
        }
    }

    public String MakeToString(List<String> list) {
        Object[] arr = list.toArray();
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<arr.length;i++) {
            builder.append((String)arr[i]);
        }
        return builder.toString();
    }

    public String handlePosition(int position) {
        position++;
        if (position > 9) {
            listid.add("" + position);
        } else {
            listid.add(""+"0"+position);
        }
        return MakeToString(listid);

    }

    public void Load_city(String provinceid) {
        Log.d("wer","2");
        dispCityByBase(provinceid);
        if (listInfo.size() != 0) {
            Toast.makeText(context, "从数据库中读取", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            current_level = CITY;
        } else {
            Toast.makeText(context, "向服务器请求", Toast.LENGTH_SHORT).show();
            SendRequest(provinceid);
        }
    }

    public void Load_county(String cityid) {
        dispCOuntyByBase(cityid);
        if (listInfo.size() != 0) {
            Toast.makeText(context, "从数据库中读取", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            current_level = COUNTY;
        } else {
            Toast.makeText(context, "向服务器请求", Toast.LENGTH_SHORT).show();
            SendRequest(cityid);
        }
    }

    public void Load_WeatherInfo(String county) {
        Log.d("wer","load weather");
        Intent intent = new Intent(DisplayList.this,Main2Activity.class);
        intent.putExtra("county",county);
        Log.d("wer", county);
        startActivity(intent);
    }

    public void Init_Load() {
        Log.d("pro", "1");
        dispProvinceByBase();
        if (listInfo.size() != 0) {
            Toast.makeText(context, "从数据库中读取", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            current_level=PROVINCE;
        } else {
            Toast.makeText(context, "向服务器请求", Toast.LENGTH_SHORT).show();
            SendRequest("");
        }
        Log.d("wer", "after init_load:" + current_level);
    }

    public String MixTheAddress(String id) {
        return "http://www.weather.com.cn/data/list3/city" + id + ".xml";
    }

    public void SendRequest(final String cityid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    Log.d("wer", "3");
                    Log.d("pro", "5");
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
                    String[] str = {response.toString(), cityid};
                    Message message = new Message();
                    message.obj=str;
                    message.what=current_level;
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void handleTheMessageForProvince(String response) {
        listInfo.clear();
        List<String> listProvince = new ArrayList<>();
        String[] province_and_id = response.split(",");
        for(int i=0;i<province_and_id.length;i++) {
            String[] provinceId=province_and_id[i].split("\\|");
            listProvince.add(provinceId[1]);
            saveProvinceIntoBase(provinceId[1],provinceId[0]);
        }
        listInfo.addAll(listProvince);
    }

    public void handleTheMessageForCity(String response,String province_id) {
        listInfo.clear();
        List<String> listCity = new ArrayList<>();
        String[] city_and_id = response.split(",");
        for(int i=0;i<city_and_id.length;i++) {
            String[] city=city_and_id[i].split("\\|");
            listCity.add(city[1]);
            saveCityIntoBase(city[1],city[0],province_id);
        }
        Log.d("wer", "in handleMessage for city: " + listCity.size());
        listInfo.addAll(listCity);
    }

    public void handleTheMessageForCounty(String response,String city_id) {
        Log.d("county", "6");
        listInfo.clear();
        String[] county_and_id = response.split(",");
        List<String> listCounty = new ArrayList<>();
        for(int i=0;i<county_and_id.length;i++) {
            String[] county=county_and_id[i].split("\\|");
            listCounty.add(county[1]);
            saveCountyIntoByBase(county[1], county[0], city_id);
        }
        Toast.makeText(context, ""+listCounty.toString(), Toast.LENGTH_SHORT).show();
        listInfo.addAll(listCounty);
    }


    public void dispProvinceByBase() {
        listInfo.clear();
        List<String> listProvince = new ArrayList<>();
        String province=null;
        db=weatherHelper.getReadableDatabase();
        Cursor cur = db.query("provinces_list", null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                province = cur.getString(cur.getColumnIndex("province"));
                listProvince.add(province);
            } while (cur.moveToNext());
        }
        listInfo.addAll(listProvince);
    }
    
    public void saveProvinceIntoBase(String province, String province_id) {
        db=weatherHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("province", province);
        values.put("province_id", province_id);
        db.insert("provinces_list", null, values);
    }

    public void saveCityIntoBase(String city, String city_id,String province_id) {
        db = weatherHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city", city);
        values.put("city_id", city_id);
        values.put("province_id", province_id);
        db.insert("city_list", null, values);
    }

    public void dispCityByBase(String province_id) {
        listInfo.clear();
        db = weatherHelper.getReadableDatabase();
        Cursor cursor=db.query("city_list",null,"province_id=?",new String[]{province_id},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                listInfo.add(cursor.getString(cursor.getColumnIndex("city")));
            } while (cursor.moveToNext());
        }
    }

    public void saveCountyIntoByBase(String county, String county_id,String city_id) {
        db = weatherHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("county", county);
        values.put("county_id", county_id);
        values.put("city_id", city_id);
        db.insert("county_list", null, values);
    }

    public void dispCOuntyByBase(String city_id) {
        listInfo.clear();
        db = weatherHelper.getReadableDatabase();
        Cursor cursor = db.query("county_list", null, "city_id=?", new String[]{city_id}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                listInfo.add(cursor.getString(cursor.getColumnIndex("county")));
            } while (cursor.moveToNext());
        }
    }


}
