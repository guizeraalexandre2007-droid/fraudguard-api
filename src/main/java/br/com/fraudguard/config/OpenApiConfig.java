package br.com.fraudguard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("FraudGuard API")
                .description("API REST acadêmica: heranca, JPA 1:N e N:N, CRUD de todas as entidades concretas")
                .version("1.0.0"))
            // URL relativa: no Codespaces, chamadas vão para o mesmo host HTTPS
            // do Swagger; localmente continuam funcionando em localhost.
            .servers(List.of(new Server()
                .url("/")
                .description("Mesmo endereço do Swagger (Codespaces ou local)")));
    }
}
