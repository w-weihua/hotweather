package com.example.wwh.hotweather.util;
/*
    Created by Joe on 2016/8/18.
    Email: wwh.cto@foxmail.com
*/

import android.text.TextUtils;

import com.example.wwh.hotweather.database.HotWeatherDB;
import com.example.wwh.hotweather.model.City;
import com.example.wwh.hotweather.model.County;
import com.example.wwh.hotweather.model.Province;

public class Utility {

    /**
     *  凡是涉及到数据库操作的，都应该上锁
     *  但在书上，只有处理省级数据的时候上锁而已
     *  其他两个方法都没有上锁，为何？
     *  同时，书上原本的判断为 if(allProvinces != null && allProvinces.length > 0)
     *  但前者貌似一直为true。这应该取决于服务器返回给我们的数据格式
     *  但在这里，我无法访问书上的那个网址，也就无法查明数据的格式
     */

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(HotWeatherDB hotWeatherDB,
                                                               String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    hotWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(HotWeatherDB hotWeatherDB,
                                                            String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    hotWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(HotWeatherDB hotWeatherDB,
                                                              String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length >0){
                for(String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    hotWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

}
