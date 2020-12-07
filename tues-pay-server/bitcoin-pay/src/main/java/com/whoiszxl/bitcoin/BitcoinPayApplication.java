package com.whoiszxl.bitcoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BitcoinPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitcoinPayApplication.class, args);
    }
}
