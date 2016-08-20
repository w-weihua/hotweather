package com.example.wwh.hotweather.receiver;
/*
    Created by Joe on 2016/8/20.
    Email: wwh.cto@foxmail.com
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.wwh.hotweather.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentAutoUdpate = new Intent(context, AutoUpdateService.class);
        context.startActivity(intentAutoUdpate);
    }
}
