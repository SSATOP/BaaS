package com.baas.securities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SecuritiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuritiesApplication.class, args);
	}
}
