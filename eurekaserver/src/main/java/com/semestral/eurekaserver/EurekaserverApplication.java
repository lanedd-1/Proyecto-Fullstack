package com.semestral.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverApplication.class, args);
		System.out.println("**************************************************");
		System.out.println("Eureka Server corriendo en: http://eureka-server:8761/eureka/");
		System.out.println("**************************************************");
	}

}
