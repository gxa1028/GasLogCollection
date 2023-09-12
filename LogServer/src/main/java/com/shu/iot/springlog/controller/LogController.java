package com.shu.iot.springlog.controller;

import com.shu.iot.springlog.service.LogService;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class LogController {
    @Autowired
    LogService logService;
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

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
        logger.info("data=",data);
        if (!isValidPath(workStationId,platName)){
            return "fail";
        }
        logService.save(workStationId,platName,new String(data));
        return "success";
    }

    @RequestMapping(value = "/fetch/{feiju}",method = RequestMethod.GET)
    @ResponseBody
    public String fetchFeiju(@PathVariable("feiju")String tp){
        return logService.fetchAllData(tp);
    }
}
