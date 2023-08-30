package com.shu.iot.logcollector;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.LineHandler;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHandler implements LineHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);
    private String monitorName;
    private String route;

    public LogHandler(String monitorName, String route) {
        this.monitorName = monitorName;
        this.route = route;
    }

    @Override
    public void handle(String line) {
        if ("".equals(line) || line == null) {
            logger.info(monitorName + "->数据为空");
            return;
        }
        try {
            HttpRequest post = HttpUtil.createPost("192.168.1.130:8082" + route);
            post.body(line, "text/plain");
            post.execute();
            logger.info(monitorName + "->发送数据:" + line);
        } catch (IORuntimeException e) {
            System.out.println("http超时");
        } catch (ClassCastException e) {
            System.out.println("classcastException");
        }
    }
}
