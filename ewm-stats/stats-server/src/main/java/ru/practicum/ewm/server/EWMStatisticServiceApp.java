package ru.practicum.ewm.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("ru.practicum.ewm.dto")
public class EWMStatisticServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EWMStatisticServiceApp.class);
    }
}