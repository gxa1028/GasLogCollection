package com.shu.iot;

import com.alibaba.fastjson.JSONObject;
import com.shu.iot.bean.KeyLogBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class KeyLogBeanTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "2222-22-22 00：00:00 [KEYLOG]4|432023091806|超声波|2023-09-18 00:00:00||正测||",
            "2222-22-22 00：00:00 [KEYLOG]4|432023091806|超声波||2023-09-18 00:00:00|完成|AAAA|AA",
            "2023-09-18 11:07:00,767 - [KEYLOG]03|433523029801| 预付费|2023/9/18 11:07:00||正测||"
    })
    public void parseLog(String line) {
        String[] keyWord = line.split("\\[KEYLOG\\]");
        String keyLog = keyWord[1];
        String[] fields = keyLog.split("\\|", -1);
        String keyJson = JSONObject.toJSONString(new KeyLogBean(fields));
        System.out.println(keyJson);
    }
}
