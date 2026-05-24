package com.joyeria.gestion_configuracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GestionConfiguracionApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionConfiguracionApplication.class, args);
	}

}
