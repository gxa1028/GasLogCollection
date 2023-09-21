package com.shu.iot.app;

import com.shu.iot.bean.MonitorProBean;
import com.shu.iot.common.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogCollectApplication {
    private static final String version = "1.0";
    private static final Logger logger = LoggerFactory.getLogger(LogCollectApplication.class);

    public static void main(String[] args) {
        logger.info("日志采集软件启动");
        logger.info("\n" +
                "   __               ___      _ _           _             \n" +
                "  / /  ___   __ _  / __\\___ | | | ___  ___| |_ ___  _ __ \n" +
                " / /  / _ \\ / _` |/ /  / _ \\| | |/ _ \\/ __| __/ _ \\| '__|\n" +
                "/ /__| (_) | (_| / /__| (_) | | |  __/ (__| || (_) | |   \n" +
                "\\____/\\___/ \\__, \\____/\\___/|_|_|\\___|\\___|\\__\\___/|_|   \n" +
                "            |___/                                        \n");
        logger.info("version:" + version);
        String sinkIP = Configuration.setting.getByGroup("log.sink.ip", "common");
        String sinkPort = Configuration.setting.getByGroup("log.sink.port", "common");
        logger.info("IP地址端口号设置为->" + sinkIP + ":" + sinkPort);

        String managerPlatPath = Configuration.setting.getByGroup("station117.managerPlat", "path");
        String csbPlat_1_Path = Configuration.setting.getByGroup("station1.csbPlat", "path");

        MonitorProBean managerPlatBean = new MonitorProBean("管理平台", "/station117/managerPlat", managerPlatPath);
        MonitorProBean csbPlat_1Bean = new MonitorProBean("超声波", "/station1/managerPlat", csbPlat_1_Path);
        switch (args[0]) {
            case "117":
                startThread(managerPlatBean);
                break;
            case "1":
                startThread(csbPlat_1Bean);
                break;
            case "2":
            case "3":
            case "4":
            case "5":
                break;
            default:
                logger.info("本次输入平台号：" + args[0] + ",请输入正确平台号!\n" +
                        "1,2,3,4,5,117");
                break;
        }
    }

    private static void startThread(MonitorProBean... beans) {
        for (MonitorProBean bean : beans) {
            FileMonitorThread fileMonitorThread = new FileMonitorThread(bean);
            new Thread(fileMonitorThread).start();
        }
    }
}
