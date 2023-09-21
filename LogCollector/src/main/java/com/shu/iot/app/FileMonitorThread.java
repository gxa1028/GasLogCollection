package com.shu.iot.app;

import com.shu.iot.bean.MonitorProBean;
import com.shu.iot.logcollector.FileMonitor;

public class FileMonitorThread implements Runnable {
    FileMonitor managerPlat;

    public FileMonitorThread(MonitorProBean proBean) {
        this.managerPlat = new FileMonitor(proBean.getMonitorName(),
                proBean.getRoute(),
                proBean.getPath());
    }

    @Override
    public void run() {
        managerPlat.run();
    }

}
