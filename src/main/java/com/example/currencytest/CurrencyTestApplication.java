package com.example.currencytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyTestApplication.class, args);
    }

}
