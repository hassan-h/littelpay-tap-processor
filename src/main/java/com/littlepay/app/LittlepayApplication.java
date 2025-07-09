package com.littlepay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.littlepay.app.config.FareProperties;

@SpringBootApplication
@EnableConfigurationProperties(FareProperties.class)
public class LittlepayApplication{

    public static void main(String[] args) {
        SpringApplication.run(LittlepayApplication.class, args);
    }
}