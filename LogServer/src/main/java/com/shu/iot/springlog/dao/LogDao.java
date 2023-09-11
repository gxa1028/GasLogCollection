package com.shu.iot.springlog.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shu.iot.springlog.common.Mapping;
import com.shu.iot.springlog.controller.LogController;
import com.shu.iot.springlog.util.MailClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class LogDao {
    private static final String DefaultDate = "未检测到当天日志";
    private static final Logger logger = LoggerFactory.getLogger(LogDao.class);
    @Autowired
    private MailClient mailClient;
    public void save(String firstName,String secondName,String data)  {
        FileOutputStream fop = null;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = simpleDateFormat.format(date);
        try {
            File file = new File(String.format("./log/%s/%s/%s/%s",firstName,currentTime,secondName));
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
            fop.write("\r\n".getBytes());
            fop.flush();
            fop.close();
        }catch (IOException ioException){
            System.out.printf(ioException.toString());
        }
    }
    public String getNameFromFile(File f){
        String[] strs = f.getPath().split("/");
        String name = strs[strs.length-1];
        return name;
    }

    public String convertTimeName(String s){
        if (s.contains("manager")){
            return "time1";
        }
        if (s.contains("fenlei")){
            return "time2";
        }

        if (s.contains("csb")){
            return "time3";
        }
        if (s.contains("xzy")){
            return "time2";
        }
        if (s.contains("ml")){
            return "time1";
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
            return "status1";
        }
        if (s.contains("fenlei")){
            return "status2";
        }
        if (s.contains("csb")){
            return "status3";
        }
        if (s.contains("xzy")){
            return "status2";
        }
        if (s.contains("ml")){
            return "status1";
        }
        if (s.contains("msb")){
            return "status4";
        }
        if (s.contains("yff")){
            return "status5";
        }
        return "error";
    }
    private boolean isAlive(long lastModify){
        if (System.currentTimeMillis() - lastModify < 5 * 60 * 1000){
            return true;
        }else {
            return false;
        }
    }
    private void sendWarningMessage(String title,String content){
        mailClient.sendMail(title,content);
    }

    public String fetchAllData(String tp){
        JSONObject finResult = new JSONObject();
        JSONObject data = generateHeader(tp);
        String path = "./log";
        File file = new File(path);
        File[] fs = file.listFiles();
        if (fs == null){
            return "fail";
        }
        Map<String,String> standardMap;
        if (tp.equals("feiju")){
            standardMap = Mapping.feijumapping;
        }else if (tp.equals("manager")){
            standardMap = Mapping.managermapping;
        }else if (tp.equals("juming")){
            standardMap = Mapping.jmMapping;
        }else{
            logger.error("invalid type!");
            return null;
        }
        List<Map<String,String>> result = new ArrayList<>();
        for (int i = 0 ; i < fs.length; i++){// 工作站层面
            File f = fs[i];
            if (f.isDirectory()){
                Map<String,String> map = new HashMap<>();
                String stationName = getNameFromFile(f);
                // 非居、居民、管理全部混在一起，需要在读取端做分类 ,mapping 负责 读取端的分类
                if (standardMap.get(stationName) == null){
                    continue;
                }
                map.put("type", standardMap.get(stationName));
                File[] dates = f.listFiles();
                for (int j = 0 ; j < dates.length; j++){// 日期层面
                      File dateFile = dates[j];
                      String dateName = dateFile.getName();
                      Date now = new Date();
                      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                      String currentTime = simpleDateFormat.format(now);
                      if (!dateName.equals(currentTime)){
                          continue;
                      }
                      File[] plats = dateFile.listFiles();
                      if (plats == null){
                          // 当天没有检测平台日志
                          break;
                      }
                      for (int k = 0 ; k < plats.length ;k++){ //检测软件层面
                          File plat = plats[k];
                          String platFileName = getNameFromFile(plat);
                          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                          Date date = new Date(plat.lastModified());
                          map.put(convertTimeName(platFileName),format.format(date));
                          if (isAlive(plat.lastModified())){
                              map.put(convertStatusName(platFileName),"✅");
                          }else{
                              String timeStampStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plat.lastModified());
                              String timeStampStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
                              sendWarningMessage("warning",String.format("管理平台异常❗️❗❗上次存活时间:%s,当前时间%s",
                                      timeStampStr1,timeStampStr2));
                              map.put(convertStatusName(platFileName),"❌");
                          }
                      }
                }
                result.add(map);
            }
        }
        Collections.sort(result, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return o1.get("type").compareTo(o2.get("type"));
            }
        });
        JSONArray js = new JSONArray();
        for (int i = 0 ; i < result.size() ; i++){
            Map<String,String> plat = result.get(i);
            JSONObject jsonPlat = new JSONObject();
            jsonObjectWrapper(jsonPlat,plat,tp);
            js.add(jsonPlat);
        }
        data.put("rows",js);
        finResult.put("msg","");
        finResult.put("data",data);
        finResult.put("status",0);
        return finResult.toJSONString();
    }

    private void jsonObjectWrapper(JSONObject jsonPlat,Map<String,String> plat,String tp){
        if (tp.equals("feiju")){
            jsonPlat.put("type",plat.get("type"));
            jsonPlat.put("time1",plat.getOrDefault("time1",DefaultDate));
            jsonPlat.put("time2",plat.getOrDefault("time2",DefaultDate));
            jsonPlat.put("time3",plat.getOrDefault("time3",DefaultDate));
            jsonPlat.put("time4",plat.getOrDefault("time4",DefaultDate));
            jsonPlat.put("time5",plat.getOrDefault("time5",DefaultDate));
            jsonPlat.put("status1",plat.getOrDefault("status1","❌"));
            jsonPlat.put("status2",plat.getOrDefault("status2","❌"));
            jsonPlat.put("status3",plat.getOrDefault("status3","❌"));
            jsonPlat.put("status4",plat.getOrDefault("status4","❌"));
            jsonPlat.put("status5",plat.getOrDefault("status5","❌"));
        }else if (tp.equals("manager")){
            jsonPlat.put("type",plat.get("type"));
            jsonPlat.put("time1",plat.getOrDefault("time1",DefaultDate));
            jsonPlat.put("time2",plat.getOrDefault("time2",DefaultDate));
            jsonPlat.put("status1",plat.getOrDefault("status1","❌"));
            jsonPlat.put("status2",plat.getOrDefault("status2","❌"));
        }else if (tp.equals("fenlei")){
            // todo :
        }

    }


    private JSONObject generateHeader(String tp){
        if (tp.equals("feiju")){
            JSONObject data = new JSONObject();
            data.put("conbineNum",1 );
            JSONArray superHeadersWrapper = new JSONArray();
            JSONArray superHeaders = new JSONArray();
            JSONObject o1 = new JSONObject();
            o1.put("colspan",1);
            superHeaders.add(o1);
            JSONObject o2 = new JSONObject();
            o2.put("name","命令检测");
            o2.put("colspan",2);
            superHeaders.add(o2);
            JSONObject o3 = new JSONObject();
            o3.put("name","修正仪");
            o3.put("colspan",2);
            superHeaders.add(o3);
            JSONObject o4 = new JSONObject();
            o4.put("name","超声波");
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
            superHeadersWrapper.add(superHeaders);
            data.put("superHeaders",superHeadersWrapper);
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
            JSONObject o14 = new JSONObject();
            o14.put("name","最近更新时间");
            o14.put("id","time5");
            columns.add(o14);
            JSONObject o15 = new JSONObject();
            o15.put("name","运行状态");
            o15.put("id","status5");
            columns.add(o15);
            data.put("columns", columns);
            return data;
        }
        else if (tp.equals("manager")){
            JSONObject data = new JSONObject();
            data.put("conbineNum",1 );
            JSONArray superHeadersWrapper = new JSONArray();
            JSONArray superHeaders = new JSONArray();
            JSONObject o1 = new JSONObject();
            o1.put("colspan",1);
            superHeaders.add(o1);
            JSONObject o2 = new JSONObject();
            o2.put("name","管理平台");
            o2.put("colspan",2);
            superHeaders.add(o2);
            JSONObject o3 = new JSONObject();
            o3.put("name","分类管理");
            o3.put("colspan",2);
            superHeaders.add(o3);
            superHeadersWrapper.add(superHeaders);
            data.put("superHeaders",superHeadersWrapper);
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
            data.put("columns", columns);
            return data;
        }else if (tp.equals("fenlei")){
            // todo:
        }
        logger.error("LogDao.generateHeader Error:invalid type");
        return null;
    }

}
