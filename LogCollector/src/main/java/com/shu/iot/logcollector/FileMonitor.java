package com.shu.iot.logcollector;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import com.shu.iot.Util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class FileMonitor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FileMonitor.class);
    private String monitorName;
    private String dirPath;
    private Tailer tailer;
    private File file;
    private String route;

    public FileMonitor() {
    }

    public FileMonitor(String monitorName, String route, String path) {
        this.monitorName = monitorName;
        this.route = route;
        this.dirPath = path;
    }

    private void init() {
        logger.info("首次启动，开始初始化监听");
        String prefix = CommonUtil.getPrefix(monitorName);
        String fileName = prefix + DateUtil.today() + ".log";
        logger.info(monitorName + "开始监听文件->" + fileName);
        file = new File(dirPath + "\\" + fileName);
        if (!file.exists()) {
            logger.info("初始化终止，" + fileName + "不存在！");
            return;
        }
        tailer = new Tailer(file, new LogHandler(monitorName, route));
        logger.info(monitorName + "启动文件增量监听");
        tailer.start();
    }

    @Override
    public void run() {
        File dir = FileUtil.file(dirPath);
        WatchMonitor watchMonitor = WatchMonitor.create(dir, WatchMonitor.ENTRY_CREATE);

        watchMonitor.setWatcher(new SimpleWatcher() {
            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                logger.info("文件发生变动，更改监听对象");
                logger.info("关闭当前监听器");
                String fileName = watchEvent.context().toString();
                logger.info(monitorName + "开始监听文件->" + fileName);
                file = new File(path + "\\" + fileName);
                tailer = new Tailer(file, new LogHandler(monitorName, route));
                logger.info(monitorName + "启动文件增量监听");
                tailer.start();
            }
        });

        watchMonitor.start();
        init();
    }
}
