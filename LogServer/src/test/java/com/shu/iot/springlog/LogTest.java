package com.shu.iot.springlog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shu.iot.springlog.common.Mapping;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogTest {
    @Test
    public void test01(){
        save1("1号机","超声波","中文");
    }

    public void save1(String firstName,String secondName,String data) {
        FileOutputStream fop = null;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
        String currentTime = simpleDateFormat.format(date);
        try {
            File file = new File(String.format("./log/%s/%s-%s",firstName,secondName,currentTime));
            File fileParent = file.getParentFile();
            if (!fileParent.exists()){
                fileParent.mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            fop = new FileOutputStream(file, true);
            byte[] contentInBytes = data.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        }catch (IOException ioException){
            System.out.printf(ioException.toString());
        }
    }
    @Test
    public void test02(){
        JSONObject totalData = new JSONObject();
        totalData.put("status",0);
        totalData.put("msg","");
        JSONObject data = new JSONObject();
        data.put("conbineNum",1 );
        JSONArray superHeaders = new JSONArray();
        JSONObject o1 = new JSONObject();
        o1.put("colspan",1);
        superHeaders.add(o1);
        JSONObject o2 = new JSONObject();
        o2.put("name","超声波");
        o2.put("colspan",2);
        superHeaders.add(o2);
        JSONObject o3 = new JSONObject();
        o3.put("name","修正仪");
        o3.put("colspan",2);
        superHeaders.add(o3);
        JSONObject o4 = new JSONObject();
        o4.put("name","命令检测");
        o4.put("colspan",2);
        superHeaders.add(o4);
        JSONObject j1 = new JSONObject();
        j1.put("name","膜式表");
        j1.put("colspan",2);
        superHeaders.add(j1);
        JSONObject j2 = new JSONObject();
        j2.put("name","预付费");
        j2.put("colspan",2);
        superHeaders.add(j2);
        data.put("superHeaders",superHeaders);
        JSONArray columns = new JSONArray();
        JSONObject o5 = new JSONObject();
        o5.put("name","工作站");
        o5.put("id","type");
        columns.add(o5);
        JSONObject o6 = new JSONObject();
        o6.put("name","最近更新时间");
        o6.put("id","time1");
        columns.add(o6);
        JSONObject o7 = new JSONObject();
        o7.put("name","运行状态");
        o7.put("id","status1");
        columns.add(o7);
        JSONObject o8 = new JSONObject();
        o8.put("name","最近更新时间");
        o8.put("id","time2");
        columns.add(o8);
        JSONObject o9 = new JSONObject();
        o9.put("name","运行状态");
        o9.put("id","status2");
        columns.add(o9);
        JSONObject o10 = new JSONObject();
        o10.put("name","最近更新时间");
        o10.put("id","time3");
        columns.add(o10);
        JSONObject o11 = new JSONObject();
        o11.put("name","运行状态");
        o11.put("id","status3");
        columns.add(o11);
        JSONObject o12 = new JSONObject();
        o12.put("name","最近更新时间");
        o12.put("id","time4");
        columns.add(o12);
        JSONObject o13 = new JSONObject();
        o13.put("name","运行状态");
        o13.put("id","status4");
        columns.add(o13);
        data.put("columns", columns);
        totalData.put("data",data);
        System.out.println(totalData.toJSONString());
    }

    public String getNameFromFile(File f){
        String[] strs = f.getPath().split("/");
        String name = strs[strs.length-1];
        return name;
    }

    public String convertTimeName(String s){
        if (s.contains("manager")){
            return "todo";
        }
        if (s.contains("csb")){
            return "time1";
        }
        if (s.contains("xzy")){
            return "time2";
        }
        if (s.contains("ml")){
            return "time3";
        }
        if (s.contains("msb")){
            return "time4";
        }
        if (s.contains("yff")){
            return "time5";
        }
        return "error";
    }

    public String convertStatusName(String s){
        if (s.contains("manager")){
            return "todo";
        }
        if (s.contains("csb")){
            return "status1";
        }
        if (s.contains("xzy")){
            return "status2";
        }
        if (s.contains("ml")){
            return "status3";
        }
        if (s.contains("msb")){
            return "status4";
        }
        if (s.contains("yff")){
            return "status5";
        }
        return "error";
    }
    @Test
    public void test03( ){
        String path = "./log";
        File file = new File(path);
        File[] fs = file.listFiles();
        List<Map<String,String >> result = new ArrayList<>();
        for (int i = 0 ; i < fs.length; i++){// 工作站层面
            File f = fs[i];
            if (f.isDirectory()){
                Map<String,String> map = new HashMap<>();
                String stationName = getNameFromFile(f);
                map.put("type",Mapping.mapping.get(stationName));
                File[] plats = f.listFiles();
                for (int j = 0 ; j < plats.length; j++){// 检测软件层面
                    File plat = plats[j];
                    String platFileName = getNameFromFile(plat);
                    map.put(convertTimeName(platFileName),String.format("%d",plat.lastModified()));
                    map.put(convertStatusName(platFileName),"mock数据");
                }
                result.add(map);
            }
        }
        for (int i = 0 ; i < result.size() ; i++){
            System.out.println(result.get(i));
        }
    }
}

/*
{
  "status": 0,
  "msg": "",
  "data": {
    "combineNum": 1,
    "superHeaders": [
      [
        {
          "colspan": 1
        },
        {
          "name": "超声波",
          "colspan": 2
        },
        {
          "name": "修正仪",
          "colspan": 2
        },
        {
          "name": "膜式表",
          "colspan": 2
        },
        {
          "name": "命令检测",
          "colspan": 2
        }
      ]
    ],
    "columns": [
      {
        "name": "工作站",
        "id": "type"
      },

      {
        "name": "最近更新时间",
        "id": "xs1"
      },
      {
        "name": "运行状态",
        "id": "xl1"
      },
      {
        "name": "最近更新时间",
        "id": "xs2"
      },
      {
        "name": "运行状态",
        "id": "xl2"
      },
      {
        "name": "最近更新时间",
        "id": "xs3"
      },
      {
        "name": "运行状态",
        "id": "xl3"
      },
      {
        "name": "最近更新时间",
        "id": "xs4"
      },
      {
        "name": "运行状态",
        "id": "xl4"
      }
    ],
    "rows": [
      {
        "type": "1号机",
        "xs": 6888,
        "xl": 6,
        "xs1": 3954,
        "xl1": 86,
        "xs2": 7366,
        "xl2": 55,
        "xs3": 2241,
        "xl3": 16,
        "xs4": 7233,
        "xl4": 83
      },
      {
        "type": "2号机",
        "xs": 2196,
        "xl": 38,
        "xs1": 6614,
        "xl1": 84,
        "xs2": 245,
        "xl2": 10,
        "xs3": 5896,
        "xl3": 20,
        "xs4": 8885,
        "xl4": 96
      },
      {
        "type": "3号机",
        "xs": 5209,
        "xl": 68,
        "xs1": 1725,
        "xl1": 66,
        "xs2": 9155,
        "xl2": 43,
        "xs3": 3615,
        "xl3": 70,
        "xs4": 2896,
        "xl4": 34
      },
      {
        "type": "4号机",
        "xs": 6233,
        "xl": 3,
        "xs1": 8693,
        "xl1": 48,
        "xs2": 5891,
        "xl2": 55,
        "xs3": 8727,
        "xl3": 15,
        "xs4": 5462,
        "xl4": 32
      },
      {
        "type": "5号机",
        "xs": 2711,
        "xl": 13,
        "xs1": 8499,
        "xl1": 28,
        "xs2": 6206,
        "xl2": 94,
        "xs3": 8233,
        "xl3": 11,
        "xs4": 3492,
        "xl4": 45
      }
    ]
  }
}
 */


/*
{
  "msg": "",
  "data": {
    "superHeaders": [
      {
        "colspan": 1
      },
      {
        "colspan": 2,
        "name": "超声波"
      },
      {
        "name": "修正仪",
        "colspan":2
      },
      {
        "colspan": 2,
        "name": "命令检测"
      }
    ],
    "conbineNum": 1,
    "columns": [
      {
        "name": "工作站",
        "id": "type"
      },
      {
        "name": "最近更新时间",
        "id": "time1"
      },
      {
        "name": "运行状态",
        "id": "status1"
      },
      {
        "name": "最近更新时间",
        "id": "time2"
      },
      {
        "name": "运行状态",
        "id": "status2"
      },
      {
        "name": "最近更新时间",
        "id": "time3"
      },
      {
        "name": "运行状态",
        "id": "status3"
      },
      {
        "name": "最近更新时间",
        "id": "time4"
      },
      {
        "name": "运行状态",
        "id": "status4"
      },
      {
        "name": "最近更新时间",
        "id": "time5"
      },
      {
        "name": "运行状态",
        "id": "status5"
      }
    ],
    "rows":[
       {
        "type": "1号机",
        "time1": 6888,
        "status1": 6,
        "time2": 3954,
        "status2": 86,
        "time3": 7366,
        "status3": 55,
        "time4": 2241,
        "status4": 16,
      }
    ]
  },
  "status": 0
}
 */