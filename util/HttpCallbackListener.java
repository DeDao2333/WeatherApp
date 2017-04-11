package com.example.java.weatherapp.util;

/**
 * Created by 59575 on 2016/12/18.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}


