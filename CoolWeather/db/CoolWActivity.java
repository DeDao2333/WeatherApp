package com.example.java.weatherapps.CoolWeather.db;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.java.weatherapps.CoolWeather.db.fragment.ItemFragment;
import com.example.java.weatherapps.CoolWeather.db.fragment.dummy.DummyContent;
import com.example.java.weatherapps.CoolWeather.db.util.HttpUtil;
import com.example.java.weatherapps.CoolWeather.db.util.Utility;
import com.example.java.weatherapps.R;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoolWActivity extends AppCompatActivity implements
        ItemFragment.OnListFragmentInteractionListener {

    private static int current_page;
    private static int state_notify;

    private static final int PRE=0;
    private static final int PROVINCE=1;
    private static final int CITY=2;
    private static final int COUNTY=3;

    private String selectedProvince;
    private String selectedCity;
    private String selectedCounty;

    private static final String PreAddress = "http://guolin.tech/api/china";
    private static List<String> LaterAddress = new ArrayList<>();

    @Override
    public void onBackPressed() {
        LaterAddress.remove(LaterAddress.size()-1);
        if (current_page == PROVINCE) {
            super.onBackPressed();
        } else if (current_page == CITY) {
            Log.d("wer", "back 1: "+selectedProvince);
            current_page=PRE;
            queryFromData(null);
        } else if (current_page == COUNTY) {
            Log.d("wer", "back 2");
            current_page = PROVINCE;
            queryFromData(selectedProvince);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool_w);

        init();

    }

    /**
     * 欠缺显式问题
     */
    public void init() {
        Fragment f = ItemFragment.newInstance(1);
        FragmentManager m = getSupportFragmentManager();
        FragmentTransaction t = m.beginTransaction();
        t.replace(R.id.frameLayout, f);
        t.commit();

        state_notify=0;
        current_page = PRE;
        if (queryFromData(null)) {
        } else {
            queryFromServe(PreAddress,null);
        }
    }

    @Override
    public void onListFragmentInteraction(final DummyContent.DummyItem item) {
        if (current_page == PROVINCE) {
            selectedProvince = item.id;
        } else if (current_page == CITY) {
            selectedCity = item.id;
        } else if (current_page == COUNTY) {
            selectedCounty = item.id;
        }

        LaterAddress.add("/"+item.id);

        if (queryFromData(item.id)) {
            Log.d("wer", "from data");
        } else {
            Log.d("wer", "from serve ");
            queryFromServe(PreAddress+ Utility.changeListIntoString(LaterAddress),item);
        }
    }


    /**
     * 从服务器中查找数据,赋值给数据库
     * @param address   地址
     * @param item  点击的项目
     */
    public void queryFromServe(String address, final DummyContent.DummyItem item) {
        Log.d("wer", "address: " + address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("wer", "failure in send okhttp");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (current_page == PRE) {
                    Utility.handleProvinceResponse(response.body().string());
                } else if (current_page == PROVINCE) {
                    Utility.handleCityResponse(response.body().string(), item.id);
                } else if (current_page == CITY) {
                    Utility.handleCountyResponse(response.body().string(), item.id);
                } else {
                    //当前为城镇页面，点击可以进行显式天气状况显式的代码
                }
                Log.d("wer", "4");
                queryFromData(item.id);    //到数据库中去读取数据刷新列表
            }
        });
    }

    /**
     * 从数据库中查找数据
     * @return  是否查找成功
     */
    public boolean queryFromData(String itemId) {
        state_notify++;
        if (current_page == PRE) {
            List<Province> p = DataSupport.findAll(Province.class);
            if (p.size() > 0) {
                //对list进行赋值的代码
                DummyContent.initProvinceList(p);
                if (state_notify > 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ItemFragment.adapter.notifyDataSetChanged();
                        }
                    });
                }
                current_page = PROVINCE;
                return true;
            }
        } else if (current_page == PROVINCE) {
            List<City> c = DataSupport.where("provinceId=?", itemId).find(City.class);
            if (c.size() > 0) {
                //对list进行赋值的代码
                Log.d("wer", "" + c.size());
                DummyContent.initCityList(c);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ItemFragment.adapter.notifyDataSetChanged();
                        current_page = CITY;
                    }
                });
                return true;
            }
        } else if (current_page == CITY) {
            List<County> ct = DataSupport.where("cityId=?", itemId).find(County.class);
            if (ct.size() > 0) {
                //对list进行赋值的代码
                DummyContent.initCountyList(ct);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ItemFragment.adapter.notifyDataSetChanged();
                        current_page = COUNTY;
                    }
                });
                return true;
            }
        } else if (current_page == COUNTY) {
            //进行天气查询的代码，从数据库中提取；
        }
        return false;
    }


}
