package com.shu.iot.springlog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
//@MapperScan("com.shu.iot.springlog.mapper")
@SpringBootApplication
@EnableScheduling
public class SpringLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringLogApplication.class, args);
    }

}
