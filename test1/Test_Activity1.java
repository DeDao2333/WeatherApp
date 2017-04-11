package com.example.java.weatherapp.test1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.java.weatherapp.R;
import com.example.java.weatherapp.model.City;
import com.example.java.weatherapp.model.CoolWeatherDB;
import com.example.java.weatherapp.util.HttpCallbackListener;
import com.example.java.weatherapp.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class Test_Activity1 extends Activity {
    private TextView title;
    private ListView list1;
    private CoolWeatherDB coolWeatherDB;
    private List<City> cityList;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String select_postion="";
    private List<String> list = new ArrayList<>();
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);
        title = (TextView)findViewById(R.id.textView2);
        list1 = (ListView)findViewById(R.id.list_view_2);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.add(select_postion);
                position=position+1;
                if(position<10){
                    select_postion=select_postion+"0"+position;
                }else{
                    select_postion=select_postion+position;
                }
                if (select_postion.length() < 6) {
                    init_load(select_postion);
                }
                if (select_postion.length() == 6) {
                    start_intent();
                }
                Toast.makeText(Test_Activity1.this, ""+select_postion.length(), Toast.LENGTH_SHORT).show();

            }
        });
        init_load("");
    }


    private void start_intent() {
        intent=new Intent(Test_Activity1.this,WeatherActivity.class);
        intent.putExtra("weather_code",""+select_postion);
        startActivity(intent);
    }
    private void init_load(String code) {
        final String address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final String[] allCities=response.split(",");
                if (allCities.length > 0 && allCities != null) {
                    dataList.clear();
                    for (String c : allCities) {
                        String[] array = c.split("\\|");
                        dataList.add(array[1]);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            list1.setSelection(0);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (list.size() > 0) {
            String current = list.remove(list.size() - 1);
            init_load(current);
            select_postion = current;
        } else {
            finish();
        }
    }
}
