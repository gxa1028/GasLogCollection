package com.shu.iot.springlog.service;

import com.alibaba.fastjson.JSONObject;
import com.shu.iot.springlog.dao.LogDao;
import com.shu.iot.springlog.entity.MeterTest;
import com.shu.iot.springlog.mapper.LogMapper;
import com.shu.iot.springlog.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

@Service
@Slf4j
public class LogService {
    @Autowired
    private LogDao logDao;
    @Autowired
    private LogMapper logMapper;
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteOutDateLog(){ // 每天凌晨1点删除三天前的日志,保留当天和最近三天日志
        log.info("定时任务触发");
        List<File> toDelete = new ArrayList<>();
        File file = new File(base);
        File[] fs = file.listFiles();
        if (fs == null){
            log.warn("LogService.deleteOutDateLogErr:空指针异常");
            return;
        }
        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String now_str = f.format(now);
        try {
            now = f.parse(now_str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE,-3);  // 左闭区间,比low小的舍弃，等于low的保留
        Date low = calendar.getTime();
        for (int i = 0 ; i < fs.length; i++){
            File station = fs[i];
            if (!station.isDirectory()){
                continue;
            }
            File[] dates = station.listFiles();
            if (dates == null){
                continue;
            }
            for (int j = 0 ; j < dates.length; j++){
                File date_file = dates[j];
                if (!date_file.isDirectory()){
                    continue;
                }
                String date_str = date_file.getName();
                Date date = null;
                try {
                    date = f.parse(date_str);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (date.compareTo(low) < 0){
                    // 添加到删除list里,后续统一删
                    toDelete.add(date_file);
                }
            }
        }
        // 实际删除
        for (int i = 0 ; i < toDelete.size(); i++){
            try {
                log.info("LogService.deleteOutDateLog:delete file:{}",toDelete.get(i).getPath());
                FileUtils.deleteDirectory(toDelete.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static final String base = "./log";
    private static final String zip = "zips";

    public void save(String firstName, String secondName, String data) {
        logDao.save(firstName, secondName, data);
    }

    public String fetchAllData(String tp) {
        return logDao.fetchAllData(tp);
    }

    public void save2(byte[] data) { //业务数据写db
        JSONObject jsonObject = (JSONObject) JSONObject.parse(data);
        MeterTest meterTest = jsonObject.toJavaObject(MeterTest.class);
        logMapper.save2(meterTest);
        if ("完成".equals(meterTest.getStatus())) {
            logMapper.save2history(meterTest);
        }
    }

    private boolean checkDate(String start_time, String end_time, String now_time) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date start = f.parse(start_time);
            Date end = f.parse(end_time);
            Date now = f.parse(now_time);
            long time1 = start.getTime();
            long time2 = end.getTime();
            long time3 = now.getTime();
            if ((time2 - time1) / (24 * 60 * 60 * 1000) > 3) { // 排除下载的天数多于三天的请求
                return false;
            }
            if ((time3 - time1) / (24 * 60 * 60 * 1000) > 3) { // 排除过早的下载请求
                return false;
            }
            if ((time2 - time1) != 0 && (time2 - time1) != 3) { //要么下载单天，要么全部下载
                return false;
            }
            return true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void download(HttpServletRequest request, HttpServletResponse response) {
        String station_name = request.getParameter("station_name"); // 要下载的工作站名称 e.g: station1、station2、all(全部下载)
        String start_time = request.getParameter("start_time");
        String end_time = request.getParameter("end_time");
        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd");
        String now_time = f.format(now);
        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        try {
            Date start = f.parse(start_time);
            Date end = f.parse(end_time);
            time1 = start.getTime();
            time2 = end.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (!checkDate(start_time, end_time, now_time)){ // 检查日期合法性
            log.warn("LogService.downloadWarning:日期不合法");
            return;
        }
        String source_file = null;
        if (station_name.equals("all")) {
            source_file = base;
        } else {
            if ((time2 - time1) == 0) {  //单天下载
                source_file = base + "/" + station_name + "/" + start_time;
            } else { // 三天的全量下载
                source_file = base + "/" + station_name;
            }
        }
        File tmp_f = new File(source_file);
        if (!tmp_f.isDirectory() && !tmp_f.isFile()){
            log.warn("LogService.downloadWarning:下载的日志不存在");
            return;
        }
        String zip_file_name = station_name + "|" + start_time + "|" + end_time;
        String dst_file = base + "/" + zip + "/" + zip_file_name + ".zip";
        createZip(source_file, dst_file); // 每次都会重新生成压缩文件，保证每次下载请求都下载最新的
        //创建一个输入流，将读取到的文件保存到输入流
        File zip_to_download = new File(dst_file);
        InputStream fis = null;
        byte[] buffer;
        try {
            fis = new BufferedInputStream(new FileInputStream(dst_file));
            buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.reset();
        // 重要，设置response的Header
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(station_name.getBytes()));
        response.setHeader("Content-Length", "" + zip_to_download.length());
        //octet-stream是二进制流传输，当不知文件类型时都可以用此属性
        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-disposition",
                    "attachment;filename=" + new String(station_name.getBytes("gbk"), "iso8859-1") + ".zip");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        //跨域请求，*代表允许全部类型
        response.setHeader("Access-Control-Allow-Origin", "*");
        //允许请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        //用来指定本次预检请求的有效期，单位为秒，在此期间不用发出另一条预检请求
        response.setHeader("Access-Control-Max-Age", "3600");
        //请求包含的字段内容，如有多个可用哪个逗号分隔如下
        response.setHeader("Access-Control-Allow-Headers", "content-type,x-requested-with,Authorization, x-ui-request,lang");
        //访问控制允许凭据，true为允许
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //创建一个输出流，用于输出文件
        OutputStream oStream = null;
        try {
            oStream = new BufferedOutputStream(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //写入输出文件
        try {
            oStream.write(buffer);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // 无论是否创建过过都创建zip
    private void createZip(String source_file, String dst_file) {
        ZipUtil.zip(source_file, dst_file);
    }

    public String query() {
        return null;
    }
}
