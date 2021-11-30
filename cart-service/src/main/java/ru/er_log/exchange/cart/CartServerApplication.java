package ru.er_log.exchange.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CartServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartServerApplication.class, args);
	}
}
