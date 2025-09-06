package com.pagam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling	  // ⚡ obligatoire pour que @Scheduled fonctionne
public class PagamApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagamApplication.class, args);
	}
}
