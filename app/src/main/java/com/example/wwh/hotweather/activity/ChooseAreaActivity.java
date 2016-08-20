package com.example.wwh.hotweather.activity;
/*
    Created by Joe on 2016/8/18.
    Email: wwh.cto@foxmail.com
*/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wwh.hotweather.R;
import com.example.wwh.hotweather.database.HotWeatherDB;
import com.example.wwh.hotweather.model.City;
import com.example.wwh.hotweather.model.County;
import com.example.wwh.hotweather.model.Province;
import com.example.wwh.hotweather.util.HttpCallbackListener;
import com.example.wwh.hotweather.util.HttpUtil;
import com.example.wwh.hotweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 *  这个类用于遍历省市县数据，并将数据显示出来
 *  一、查询数据
 *      ①访问服务器取得数据
 *      ②存储数据进相应的数据表
 *      ③从数据库读取数据
 *  二、将数据显示出来：
 *      ①设置适配器和显示列表
 *      ②根据用户的单击事件
 *      ③查询下一级的数据
 */
public class ChooseAreaActivity extends Activity {

    private TextView textView;
    private ListView listView;
    private HotWeatherDB db;                                   //数据库
    private ArrayAdapter<String> adapter;                     //适配器
    private List<String> dataList = new ArrayList<String>(); //适配器对应的数据
    private ProgressDialog progressDialog;                  //提醒用户的进度条对话框

    /**
     *  将从数据库查询到的数据暂
     *  且存入一个相应类型的链表
     *  省列表、市列表、县列表
     */
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    /**
     *  选中的省份、城市
     */
    private Province selectedProvince;
    private City selectedCity;

    /**
     *  记录查询的级别
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    /**
     *  当前选中的级别
     */
    private int currentLevel;

    //判断是否是从WeatherActivity直接跳转过来的
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        //判断之前有无选择城市，假如有就直接跳到天气信息页面
        //已经选择了城市并且不是从WeatherActivity跳过来的才打开WeatherActivity
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        textView = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        //设置适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        db = HotWeatherDB.getInstance(this);        //获取数据库实例
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(i).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();   //加载省份数据
    }

    /**
     *  查询全国所有的省，优先从数据库查询，如果没有再渠道服务器上查询
     */
    private void queryProvinces(){
        provinceList = db.loadProvinces();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else{
            queryFromServer(null, "province");
        }
    }

    /**
     *  查询选中省的所有的市，优先从数据库查询，如果没有再渠道服务器上查询
     */
    private void queryCities(){
        cityList = db.loadCities(selectedProvince.getId());
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     *  查询选中市的所有的县，优先从数据库查询，如果没有再渠道服务器上查询
     */
    private void queryCounties(){
        countyList = db.loadCounties(selectedCity.getId());
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     *  根据传入的倒好和类型从服务器上查询省市县数据
     */
    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            // 假如是市和县，则拼接网址
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();   //显示进度对话框，提示用户稍等
        //传入address的时候，已经查询完毕，接着使用handle方法来处理数据
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(db, response);
                } else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(db, response, selectedProvince.getId());
                } else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(db, response, selectedCity.getId());
                }
                if(result){
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();      //加载完毕，关闭对话框
                            if("province".equals(type)){
                                queryProvinces();
                            } else if("city".equals(type)){
                                queryCities();
                            } else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败！",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    /**
     *  显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     *  关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     *  捕获Back按键，根据当前的级别来判断，此时
     *  应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        } else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        } else {
            //按下返回键的时候，判断是否由WeatherActivity跳过来的
            if(isFromWeatherActivity){
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        }
    }

}

