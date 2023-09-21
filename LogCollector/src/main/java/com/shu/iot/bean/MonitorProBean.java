package com.shu.iot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonitorProBean {
    private String monitorName;
    private String route;
    private String path;
}
