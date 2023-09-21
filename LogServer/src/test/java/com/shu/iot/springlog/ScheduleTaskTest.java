package com.shu.iot.springlog;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest
public class ScheduleTaskTest {
    private static final String base = "./log";
    @Test
    public void test01(){
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
        calendar.add(Calendar.DAY_OF_YEAR,-3);  // 左闭区间,比low小的舍弃，等于low的保留
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
}
