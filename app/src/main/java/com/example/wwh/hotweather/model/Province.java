package com.example.wwh.hotweather.model;
/*
    Created by Joe on 2016/8/17.
    Email: wwh.cto@foxmail.com
*/

// 对应Province表的实体类，主要就是set()和get()方法
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

}
