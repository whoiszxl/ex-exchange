package com.whoiszxl.erc20;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(value = "com.whoiszxl.core")
@ComponentScan(value = "com.whoiszxl.core.exception")
@ComponentScan(value = "com.whoiszxl.erc20")
@ComponentScan(value = "com.whoiszxl.ethereum")
@EntityScan(basePackages = {"com.whoiszxl.core.entity"})
@EnableJpaRepositories(basePackages = {"com.whoiszxl.core.repository"})
public class Erc20Application {

    public static void main(String[] args) {
        SpringApplication.run(Erc20Application.class, args);
    }
}
