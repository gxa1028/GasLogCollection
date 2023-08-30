package com.shu.iot.app;

import com.shu.iot.logcollector.FileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCollectApplication {
    private static final Logger logger = LoggerFactory.getLogger(LogCollectApplication.class);

    public static void main(String[] args) {
        logger.info("日志采集软件启动");
        FileMonitor managerPlat = new FileMonitor("管理平台",
                "/station117/managerPlat",
                "E:\\Workspaces\\VisualStudioProjects\\NewPlat_new_auto-yff-v1.13\\NewPlat_new_auto(更新版)\\NewPlat\\bin\\Debug\\New\\TestInfo");
        managerPlat.run();
    }
}
