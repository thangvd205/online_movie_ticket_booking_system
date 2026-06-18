package com.thangvd.cinepass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinepassApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinepassApplication.class, args);
	}

}
