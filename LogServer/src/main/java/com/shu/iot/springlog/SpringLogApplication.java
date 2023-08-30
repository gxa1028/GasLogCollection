package com.shu.iot.springlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class SpringLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringLogApplication.class, args);
    }

}
