package com.example.wwh.hotweather.util;
/*
    Created by Joe on 2016/8/18.
    Email: wwh.cto@foxmail.com
*/

public interface HttpCallbackListener {

    /**
     *  该接口用于回调结果
     */
    void onFinish(String response);

    void onError(Exception e);

}
