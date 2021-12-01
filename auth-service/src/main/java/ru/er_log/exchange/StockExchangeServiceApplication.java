package ru.er_log.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockExchangeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockExchangeServiceApplication.class, args);
    }
}
