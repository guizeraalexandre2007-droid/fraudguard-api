package br.com.fraudguard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openApi() {
        return new OpenAPI().info(new Info().title("FraudGuard API")
            .description("API REST acadêmica: heranca, JPA 1:N e N:N, CRUD de todas as entidades concretas")
            .version("1.0.0"));
    }
}
