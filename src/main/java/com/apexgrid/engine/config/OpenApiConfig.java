package com.apexgrid.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ApexGrid Esports Tournament Engine API")
                        .version("1.0.0")
                        .description("Backend engine managing tournament rosters, single-elimination bracket generation, score validation, and analytics leaderboards.")
                        .contact(new Contact()
                                .name("ApexGrid Development Team")
                                .email("dev@apexgrid.io")));
    }
}
