package com.joyeria.gestion_envio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionEnvioApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionEnvioApplication.class, args);
	}

}
