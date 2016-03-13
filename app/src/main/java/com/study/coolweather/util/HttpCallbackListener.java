package com.study.coolweather.util;

/**
 * Created by WXB506 on 2016/3/13.
 */
public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
