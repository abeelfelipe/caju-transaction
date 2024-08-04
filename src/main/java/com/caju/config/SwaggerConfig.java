package com.caju.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Caju Transaction API")
                        .description("Caju Transaction API")
                        .version("v1.0")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License()
                                .name("Spring doc License")
                                .url("http://springdoc.org"))
                        .contact(new Contact()));
    }
}
