package com.example.java.weatherapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.java.weatherapp.R;
import com.example.java.weatherapp.model.City;
import com.example.java.weatherapp.model.CoolWeatherDB;
import com.example.java.weatherapp.model.Country;
import com.example.java.weatherapp.model.Province;
import com.example.java.weatherapp.util.HttpCallbackListener;
import com.example.java.weatherapp.util.HttpUtil;
import com.example.java.weatherapp.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 59575 on 2016/12/19.
 */

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countyList;
    private Province select_province;
    private City select_city;
    private int current_level;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_main);
        listView = (ListView) findViewById(R.id.list1);
        titleText = (TextView) findViewById(R.id.titile11);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        Log.d("wer", "1");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current_level == LEVEL_PROVINCE) {
                    select_province = provinceList.get(position);
                    queryCities();
                } else if (current_level == LEVEL_CITY) {
                    select_city = cityList.get(position);
                    queryCounty();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        provinceList=coolWeatherDB.loadProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            current_level = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCities(select_province.getId());
        if (cityList.size() > 0) {
            Log.d("wer","query city1");
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            Log.d("wer","query city2");
            listView.setSelection(0);
            titleText.setText(select_province.getProvinceName());
            current_level = LEVEL_CITY;
        }else{
            Toast.makeText(this, "query city from server", Toast.LENGTH_SHORT).show();
            queryFromServer(select_province.getProvinceCode(),"city");
        }
    }

    private void queryCounty() {
        countyList = coolWeatherDB.loadCountry(select_city.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (Country county : countyList) {
                dataList.add(county.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(select_city.getCityName());
            current_level=LEVEL_COUNTY;
        }else{
            queryFromServer(select_city.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        Log.d("wer","server 1");
        final String address;
        if (!TextUtils.isEmpty(code)) {
            Log.d("wer","server 12");
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
//        Toast.makeText(this, "server...1", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, ""+address, Toast.LENGTH_LONG).show();
//        showProgressDialog("Loading...1");
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    Log.d("wer","query_server city1");
                    showProgressDialog("Loading...2");
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, select_province.getId());
                    showProgressDialog("Loading...3");
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(coolWeatherDB, response, select_city.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChooseAreaActivity.this, "result=true", Toast.LENGTH_SHORT).show();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        Toast.makeText(this, "server...2", Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog(String ad) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(ad);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (current_level == LEVEL_COUNTY) {
            queryCities();
        } else if (current_level == LEVEL_CITY) {
            queryProvince();
        }else {
            finish();
        }
    }
}
