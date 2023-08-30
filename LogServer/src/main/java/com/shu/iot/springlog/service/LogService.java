package com.shu.iot.springlog.service;

import com.shu.iot.springlog.dao.LogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LogService {
     @Autowired
     private LogDao logDao;
     public void save(String firstName,String secondName,String data){
          logDao.save(firstName,secondName,data);
     }
     public String fetchAllData(){
          return logDao.fetchAllData();
     }
     public String query(){
          return null;
     }
}
