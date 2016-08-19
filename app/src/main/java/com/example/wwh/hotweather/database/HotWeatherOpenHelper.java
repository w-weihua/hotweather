package com.example.wwh.hotweather.database;
/*
    Created by Joe on 2016/8/17.
    Email: wwh.cto@foxmail.com
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotWeatherOpenHelper extends SQLiteOpenHelper {

    /*
    *  SQLiteOpenHelper帮助类：可对数据库进行创建和升级
    *  首先，我们得继承它，因为它是一个抽象类
    *  然后，重写构造方法和onCreate()和onUpgrade()两个方法
    *  接着，new一个实例出来，构造方法的参数为：
    *  Context context, String name, SQLiteDatabase.CursorFactory factory, int version
    *  第一个为上下文，第二个为数据库名称，第三个设置为null即可，第四个为数据库版本号
    *  最后，使用它的两个实例方法中的一个来创建数据库
    *  getReadableDatabase() 或 getWritableDatabase()
    *  注意：需要在重写的这个帮助类里，则需要加上建表的逻辑
    *  并且发现SQLite的数据类型和一般的SQL不同：
    *  integer：整型，real：浮点型，text：文本类型，blob：二进制类型
    * */

    // province 建表语句
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";

    // city  建表语句
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    // county  建表语句
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";

    // 必须重写的构造方法
    public HotWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 创建数据库的逻辑
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    // 升级数据库的逻辑
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
