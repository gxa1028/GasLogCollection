package com.shu.iot.logcollector;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.LineHandler;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.shu.iot.bean.KeyLogBean;
import com.shu.iot.common.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHandler implements LineHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);
    private String monitorName;
    private String route;
    private String sinkIP;
    private String sinkPort;

    public LogHandler(String monitorName, String route) {
        this.monitorName = monitorName;
        this.route = route;
        this.sinkIP = Configuration.setting.getByGroup("log.sink.ip", "common");
        this.sinkPort = Configuration.setting.getByGroup("log.sink.port", "common");
    }

    @Override
    public void handle(String line) {
        if ("".equals(line) || line == null) {
            logger.info(monitorName + "->数据为空");
            return;
        }
        try {
            if (line.contains("[KEYLOG]")) {
                KeyLogBean keyLogBean = parseLog(line);
                String keyJson = JSONObject.toJSONString(keyLogBean);
                HttpRequest post = HttpUtil.createPost(sinkIP + ":" + sinkPort + "/keylog");
                post.body(keyJson, "text/plain");
                post.execute();
                logger.info(monitorName + "[KEYLOG]关键日志:" + keyJson);
            } else {
                HttpRequest post = HttpUtil.createPost(sinkIP + ":" + sinkPort + route);
                post.body(line, "text/plain");
                post.execute();
                logger.info(monitorName + "->发送数据:" + line);
            }
        } catch (IORuntimeException e) {
            logger.error("Http超时");
        } catch (ClassCastException e) {
            logger.error("类型转换异常");
        } catch (Exception e) {
            logger.error("其它异常" + e.getMessage());
        }
    }

    private KeyLogBean parseLog(String line) {
        String[] keyWord = line.split("\\[KEYLOG\\]");
        String keyLog = keyWord[1];
        String[] fields = keyLog.split("\\|", -1);
        return new KeyLogBean(fields);
    }
}
