package com.shu.iot.springlog.mapper;

import com.shu.iot.springlog.entity.MeterTest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    void save2(MeterTest meterTest);

    void save2history(MeterTest meterTest);
}
