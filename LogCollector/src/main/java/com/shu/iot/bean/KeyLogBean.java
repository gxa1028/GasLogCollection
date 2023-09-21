package com.shu.iot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyLogBean {
    private String id;
    private String meterId;
    private String platType;
    private String startTime;
    private String endTime;
    private String status;
    private String detail;
    private String result;

    public KeyLogBean(String... fields) {
        this.id = fields[0];
        this.meterId = fields[1];
        this.platType = fields[2];
        this.startTime = fields[3];
        this.endTime = fields[4];
        this.status = fields[5];
        this.detail = fields[6];
        this.result = fields[7];
    }

}
