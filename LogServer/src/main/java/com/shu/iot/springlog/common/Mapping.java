package com.shu.iot.springlog.common;

import java.util.HashMap;
import java.util.Map;

public class Mapping {
    public static Map<String,String> feijumapping = new HashMap<>();

    public static Map<String,String> jmMapping = new HashMap<>();

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


    }

}
