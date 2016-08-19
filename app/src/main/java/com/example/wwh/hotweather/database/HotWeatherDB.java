package com.example.wwh.hotweather.database;
/*
    Created by Joe on 2016/8/18.
    Email: wwh.cto@foxmail.com
*/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.wwh.hotweather.model.City;
import com.example.wwh.hotweather.model.County;
import com.example.wwh.hotweather.model.Province;
import java.util.ArrayList;
import java.util.List;

public class HotWeatherDB {

    public static final String DB_NAME = "hot_weather";    //数据库名字
    public static final int DB_VERSION = 1;                //数据库版本
    private SQLiteDatabase db;                                //真正的数据库
    private static HotWeatherDB hotWeatherDB;                //设置为静态方便访问

    /**
    *   将构造方法私有化
    */
    private HotWeatherDB(Context context){
        HotWeatherOpenHelper dbHelper = new HotWeatherOpenHelper(
                context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     *  获取HotWeatherDB的实例
     *  同时进行枷锁，只允许
     *  产生一个数据库
     */
    public synchronized static HotWeatherDB getInstance(Context context){
        if(hotWeatherDB == null){
            hotWeatherDB = new HotWeatherDB(context);
        }
        return hotWeatherDB;
    }

    /**
     *  将Province实例存储进数据库中的Province表
     *  构建ContentValues对象，并使用insert方法
     *  插入到表中，可根据具体提示来输入参数
     */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_code", province.getProvinceCode());
            values.put("province_name", province.getProvinceName());
            db.insert("Province", null, values);
        }
    }

    /**
     *  从数据库读取全国所有的省份信息
     *  即用query方法查询Province表，得到一个Cursor对象
     *  接着调用它的moveToFirst()方法将数据的指针移动到第一行的位置
     *  然后进入到下一个循环当中，去遍历查询到的每一行数据
     *  在这个循环中可以通过Cursor的getColumnIndex()方法获取到某一列
     *  在表中对应的位置索引
     *  然后将这个索引传入到相应的取值方法中
     *  就可以得到数据库中读取到的数据了
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }


    /**
     *  将City实例存储进数据库中的City表
     */
    public void saveCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     *  从数据库读取某省下所有的城市信息
     *  注意传入省份的ID，然后利用query的条件查询
     *  "province_id = ?", new String[]{String.valueOf(provinceId)}
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null,
                "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     *  将County实例存储进数据库中的County表
     */
    public void saveCounty(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_code", county.getCountyCode());
            values.put("county_name", county.getCountyName());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     *  从数据库读取某市下所有的城镇信息
     *  注意传入城市的ID即可
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null,
                "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }

}
