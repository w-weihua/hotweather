package com.example.wwh.hotweather.util;
/*
    Created by Joe on 2016/8/18.
    Email: wwh.cto@foxmail.com
*/

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    /**
     *  该类用于和服务器的交互
     *  开启线程发出Http请求
     *  然后使用一个接口来
     *  回调服务器返回的结果
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有两个方法，HttpURLConnection和HttpClient，后者使用不了
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    // 一定要在这里进行转型
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                    // 回调onFinish()方法
                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    // 回调onError()方法
                    if(listener != null){
                        listener.onError(e);
                    }
                } finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
