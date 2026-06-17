package com.semestral.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverApplication.class, args);
		System.out.println("**************************************************");
		System.out.println("Eureka Server corriendo en: http://localhost:8761");
		System.out.println("**************************************************");
	}

}
