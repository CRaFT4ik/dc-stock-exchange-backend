package ru.er_log.exchange.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@EnableEurekaServer
@SpringBootApplication
public class OldEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OldEurekaServerApplication.class, args);
    }
}
