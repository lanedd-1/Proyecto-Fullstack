package com.semestral.gestion_direccion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionDireccionApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDireccionApplication.class, args);
	}

}
