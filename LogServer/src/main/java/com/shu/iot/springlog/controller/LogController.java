package com.shu.iot.springlog.controller;

import com.shu.iot.springlog.service.LogService;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@Slf4j
public class LogController {
    @Autowired
    LogService logService;

    private boolean isValidPath(String workStationId,String platName){
        if (workStationId.startsWith("jm")){
            return true;
        }else if(workStationId.startsWith("station")){
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/{workStationId}/{platName}",method = RequestMethod.POST)
    @ResponseBody
    public String dataUpload(@PathVariable("workStationId")String workStationId, @PathVariable("platName")String platName
                            , @RequestBody byte[] data){
        log.info("upload:{}",data);
        if (!isValidPath(workStationId,platName)){
            return "fail";
        }
        logService.save(workStationId,platName,new String(data));
        return "success";
    }

    @RequestMapping(value = "/fetch/{feiju}",method = RequestMethod.GET)
    @ResponseBody
    public String fetchFeiju(@PathVariable("feiju")String tp){
        log.info("fetch:{}",tp);
        return logService.fetchAllData(tp);
    }

    @RequestMapping(value = "/keylog",method = RequestMethod.POST)
    @ResponseBody
    public String dataUpload2(@RequestBody byte[] data){
        log.info("data:",data);
        logService.save2(data);
        return "success";
    }
    // 仅支持按照工作站维度下载
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response){
        log.info("download/station_name:{},start_time:{},end_time:{}",
                request.getParameter("station_name"),request.getParameter("start_time"),request.getParameter("end_time"));
        logService.download(request,response);
    }
}
