package com.shu.iot.springlog.common;

import java.util.HashMap;
import java.util.Map;

public class Mapping {
    public static Map<String,String> mapping = new HashMap<>();

    static {
        mapping.put("station1","1号机");
        mapping.put("station2","2号机");
        mapping.put("station3","3号机");
        mapping.put("station4","4号机");
        mapping.put("station5","5号机");
        mapping.put("station117","117");
        mapping.put("managerPlat","管理平台");
        mapping.put("xzyPlat","修正仪");
        mapping.put("msbPlat","膜式表");
        mapping.put("yffPlat","预付费");
        mapping.put("csbPlat","超声波");
    }

}
