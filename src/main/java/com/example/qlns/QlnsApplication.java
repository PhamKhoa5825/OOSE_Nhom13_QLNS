package com.example.qlns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QlnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(QlnsApplication.class, args);
    }

}
