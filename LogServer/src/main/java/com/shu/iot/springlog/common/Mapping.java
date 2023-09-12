package com.shu.iot.springlog.common;

import java.util.*;

public class Mapping {
    public static Map<String,String> feijumapping = new HashMap<>();

    public static Map<String,String> jmMapping_First = new HashMap<>();

    public static Map<String,String> jmMapping_Second = new HashMap<>();
    public static Map<String,String> managermapping = new HashMap<>();

    static {
        feijumapping.put("station1","1号机");
        feijumapping.put("station2","2号机");
        feijumapping.put("station3","3号机");
        feijumapping.put("station4","4号机");
        feijumapping.put("station5","5号机");
        feijumapping.put("xzyPlat","修正仪");
        feijumapping.put("msbPlat","膜式表");
        feijumapping.put("yffPlat","预付费");
        feijumapping.put("csbPlat","超声波");
        managermapping.put("station117","117");
        managermapping.put("manager","管理平台");
        managermapping.put("fenlei","分类管理");

        jmMapping_First.put("jm","居民");
        jmMapping_First.put("one","1号点");
        jmMapping_First.put("two","2号点");
        jmMapping_First.put("three","3号点");
        jmMapping_First.put("four","4号点");

        jmMapping_Second.put("jm","居民");
        jmMapping_Second.put("one","1.0");
        jmMapping_Second.put("two","2.0");
        jmMapping_Second.put("three","3.0");
        jmMapping_Second.put("four","4.0");
        jmMapping_Second.put("csb","超声波");
        jmMapping_Second.put("gk","工况");
    }

}
