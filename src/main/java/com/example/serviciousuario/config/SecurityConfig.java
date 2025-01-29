package com.example.serviciousuario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/v1/usuario/**").hasRole("ADMIN") // Permitir todos los GET
                .requestMatchers(HttpMethod.POST, "/api/v1/usuario/**").permitAll() // Permitir todos los POST
                .requestMatchers(HttpMethod.PUT, "/api/v1/usuario/**").permitAll() // Permitir todos los PUT
                .requestMatchers(HttpMethod.DELETE, "/api/v1/usuario/**").permitAll() // Permitir todos los DELETE
                .anyRequest().authenticated() // Requerir autenticación para otros endpoints
            )
            .httpBasic(httpBasic -> {}); // Configurar autenticación básica sin advertencias

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usar BCrypt para cifrar contraseñas
    }
}
