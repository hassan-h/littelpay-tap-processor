package com.littlepay.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;
import com.littlepay.app.util.CsvReader;
import com.littlepay.app.util.CsvWriter;

@Configuration
public class AppConfig {

    @Bean
    public CsvReader<Tap> tapCsvReader() {
        return new CsvReader<>(Tap.class);
    }

    @Bean
    public CsvWriter<Trip> tripCsvWriter() {
        return new CsvWriter<>(Trip.class);
    }
}
