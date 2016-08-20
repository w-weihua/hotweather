package com.example.wwh.hotweather.activity;
/*
    Created by Joe on 2016/8/20.
    Email: wwh.cto@foxmail.com
*/

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wwh.hotweather.R;
import com.example.wwh.hotweather.util.HttpCallbackListener;
import com.example.wwh.hotweather.util.HttpUtil;
import com.example.wwh.hotweather.util.Utility;

/**
 *  先拼接网址，然后分解得到天气代号
 *  然后调用Utility里访问服务器的方法
 *  查询信息并存储信息到SharedPreferences
 *  接着读取SharedPreferences里的数据
 *  显示到界面上
 *
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    private TextView textViewCityName;          //城市名
    private TextView textViewPTime;             //发布时间
    private TextView textViewCurrentDate;      //当前时间
    private TextView textViewWeatherDesc;      //天气详情
    private TextView textViewTemp1;            //最低温度
    private TextView textViewTemp2;            //最高温度
    private Button switchCity;                 //切换城市按钮
    private Button refreshWeather;            //刷新天气按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各个控件
        textViewCityName = (TextView) findViewById(R.id.city_name);
        textViewPTime = (TextView) findViewById(R.id.publish_time);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        textViewCurrentDate = (TextView) findViewById(R.id.current_date);
        textViewWeatherDesc = (TextView) findViewById(R.id.weather_desp);
        textViewTemp1 = (TextView) findViewById(R.id.temp1);
        textViewTemp2 = (TextView) findViewById(R.id.temp2);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        //取出传来的countyCode
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            textViewPTime.setText("同步中...");
            //查询天气的同时，部分UI不可见
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            textViewCityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级代号就直接显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                //存一个标签，日后别有用途
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                //启动另外一个活动的同时销毁原本的活动
                finish();
                break;
            case R.id.refresh_weather:
                textViewPTime.setText("同步中...");
                //取出当前weatherCode，并访问服务器查询最新天气
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    //查询县级代号所对应的天气代号
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    //查询天气代号所对应的天气
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    //根据传入的地址和类型去向服务器查询天气代号或者天气信息
    //这里可以优化，因为天气代号可以一直保存在本地的
    public void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if(array != null && array.length==2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)){
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    //返回到主线程中更新UI变化
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewPTime.setText("同步失败！");
                    }
                });
            }
        });
    }

    //从SharedPreferences文件中读取存储的天气信息，并显示到界面上
    private void showWeather(){
        //注意获取SharedPreferences实例的三种方法
        SharedPreferences spfs = PreferenceManager.getDefaultSharedPreferences(this);
        textViewCityName.setText(spfs.getString("city_name", ""));
        textViewTemp1.setText(spfs.getString("temp1", ""));
        textViewTemp2.setText(spfs.getString("temp2", ""));
        textViewWeatherDesc.setText(spfs.getString("weather_desc", ""));
        textViewPTime.setText("今天"+ spfs.getString("publish_time", "") +"发布");
        textViewCurrentDate.setText(spfs.getString("current_date", ""));
        //设置控件可见
        weatherInfoLayout.setVisibility(View.VISIBLE);
        textViewCityName.setVisibility(View.VISIBLE);
    }

}
