package com.shu.iot.springlog.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MeterTest {
    private String id;
    private String meterId;
    private String platType;
    private String startTime;
    private String endTime;
    private String status;
    private String detail;
    private String result;
}
