package com.udea.parcialFinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Protege los endpoints de Actuator con autenticación Basic.
 *
 * <ul>
 *   <li>{@code /actuator/health} queda abierto para comprobaciones de disponibilidad.</li>
 *   <li>El resto de {@code /actuator/**} (incluido {@code /actuator/prometheus}) exige el usuario de métricas.</li>
 * </ul>
 *
 * Esta cadena solo aplica a {@code /actuator/**}: las demás rutas de la aplicación
 * no pasan por ningún filtro de seguridad y se comportan igual que antes.
 */
@Configuration
public class MetricsSecurityConfig {

    static final String METRICS_ROLE = "METRICS";

    @Bean
    SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .anyRequest().hasRole(METRICS_ROLE))
                .httpBasic(Customizer.withDefaults())
                // Prometheus no maneja sesiones ni tokens CSRF; cada petición trae sus credenciales
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService metricsUserDetailsService(
            @Value("${metrics.user}") String username,
            @Value("${metrics.password}") String password,
            PasswordEncoder passwordEncoder) {
        if (password.isBlank()) {
            throw new IllegalStateException("METRICS_PASSWORD no puede estar vacía");
        }
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(METRICS_ROLE)
                .build());
    }
}
