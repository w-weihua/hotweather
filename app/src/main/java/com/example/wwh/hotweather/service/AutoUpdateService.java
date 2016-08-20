package com.example.wwh.hotweather.service;
/*
    Created by Joe on 2016/8/20.
    Email: wwh.cto@foxmail.com
*/

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.wwh.hotweather.receiver.AutoUpdateReceiver;
import com.example.wwh.hotweather.util.HttpCallbackListener;
import com.example.wwh.hotweather.util.HttpUtil;
import com.example.wwh.hotweather.util.Utility;

/**
 *  使用Service，需要重写以下两个方法
 *  同时在onStartCommand里写处理逻辑
 */
public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //更新天气数据
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000;    //这是1小时的毫秒数
        long triggerTime = SystemClock.elapsedRealtime() + 8 * anHour;
        //构建出一个PendingIntent对象
        Intent in = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //更新天气数据
    private void updateWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = sharedPreferences.getString("weather_code", "");
        String address ="http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
