package com.kikitraiteur.api_kikitraiteur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApiKikitraiteurApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiKikitraiteurApplication.class, args);
	}

}
