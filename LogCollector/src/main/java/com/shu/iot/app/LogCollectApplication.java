package com.shu.iot.app;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.setting.Setting;
import com.shu.iot.logcollector.FileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;

public class LogCollectApplication {
    private static final Logger logger = LoggerFactory.getLogger(LogCollectApplication.class);

    public static void main(String[] args) {
        logger.info("日志采集软件启动");

        Setting setting = new Setting("conf.setting");
        String managerPlatPath = setting.getByGroup("path.managerPlat", "path");

        FileMonitor managerPlat = new FileMonitor("管理平台",
//                "/station117/managerPlat",
                "/station5/csbPlat",
                managerPlatPath);
        managerPlat.run();
    }
}
