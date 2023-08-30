package com.shu.iot.springlog.controller;

import com.shu.iot.springlog.service.LogService;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

@Controller
public class LogController {
    @Autowired
    LogService logService;
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @RequestMapping(value = "/{workStationId}/{platName}",method = RequestMethod.POST)
    @ResponseBody
    public String dataUpload(@PathVariable("workStationId")String workStationId, @PathVariable("platName")String platName,
                           @RequestBody byte[] data){
        logger.info("data=",data);
        logService.save(workStationId,platName,new String(data));
        return "success";
    }

    @RequestMapping(value = "/fetch",method = RequestMethod.GET)
    @ResponseBody
    public String fetchAllData(){
        return logService.fetchAllData();
    }
}
