package com.joyeria.gestion_envio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("ms-envio API")
                        .version("1.0.0")
                        .description("API REST para la gestión de envíos y su historial de estados")
                        .contact(new Contact()
                                .name("DSY1103-005D")
                                .email("ejemplo@gmail.com")))
                .addServersItem(new Server()
                                .url("http://localhost:8082")
                                .description("Servidor de desarrollo local"));
    }
}