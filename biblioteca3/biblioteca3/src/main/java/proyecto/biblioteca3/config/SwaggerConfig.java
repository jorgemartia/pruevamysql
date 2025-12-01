package proyecto.biblioteca3.config;

import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("API Biblioteca")
                .version("1.0.0")
                .description("Sistema de Gestión de Biblioteca");
    }
}
