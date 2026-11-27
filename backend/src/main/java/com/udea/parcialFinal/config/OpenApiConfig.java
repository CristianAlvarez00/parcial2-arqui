package com.udea.parcialFinal.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI parcialFinalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión Académica - Parcial Final Arquisoft")
                        .version("v1")
                        .description("API RESTful para el registro y consulta paginada de estudiantes, materias "
                                + "y calificaciones académicas, con soporte HATEOAS, caché en Redis y observabilidad.")
                        .contact(new Contact()
                                .name("Equipo Arquitectura de Software - UdeA")))
                .tags(List.of(
                        new Tag().name("Estudiantes").description("Registro y consulta de estudiantes"),
                        new Tag().name("Notas").description("Registro y consulta paginada de notas por estudiante"),
                        new Tag().name("Materias").description("Consulta paginada e individual del catálogo de materias")
                ));
    }

    /**
     * Garantiza que la documentación OpenAPI y Swagger UI sean siempre públicas,
     * incluso si se agregan cadenas de seguridad adicionales para otras rutas.
     */
    @Bean
    @Order(1)
    SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
