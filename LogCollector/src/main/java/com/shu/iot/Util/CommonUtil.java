package com.shu.iot.Util;

import com.shu.iot.common.Constant;

public class CommonUtil {
    public static String getPrefix(String monitorName) {
        switch (monitorName) {
            case "管理平台":
                return Constant.MANAGER_PLAT_LOG_PREFIX;
            case "命令":
                return Constant.COMMAND_PLAT_LOG_PREFIX;
            case "超声波":
            case "膜式表":
            case "修正仪":
            case "预付费":
                return Constant.TEST_PLAT_LOG_PREFIX;
            default:
                return "NULL";
        }
    }
}
