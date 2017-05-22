package com.example.java.weatherapps.CoolWeather.db.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 59575 on 2017/5/11.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request mRequest = new Request.Builder().url(address).build();
        mOkHttpClient.newCall(mRequest).enqueue(callback);
    }
}
