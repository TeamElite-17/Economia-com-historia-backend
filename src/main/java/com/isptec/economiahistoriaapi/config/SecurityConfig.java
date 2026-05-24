package com.isptec.economiahistoriaapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth - público (login, register, logout)
                        .requestMatchers("/v1/auth/**").permitAll()
                        // Swagger - público
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Actuator - público
                        .requestMatchers("/actuator/**").permitAll()

                        // Categorias e Tópicos - GET público
                        .requestMatchers(HttpMethod.GET, "/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/topics/**").permitAll()

                        // === Gestão de utilizadores (UC04-UC06) ===
                        .requestMatchers("/v1/admin/**").hasAnyRole("SUPERADMIN", "ADMIN")

                        // === Fluxo editorial (UC07-UC09) ===
                        .requestMatchers(HttpMethod.POST, "/v1/content").hasRole("ESCRITOR")
                        .requestMatchers(HttpMethod.PATCH, "/v1/content/*/submit").hasAnyRole("ESCRITOR")
                        .requestMatchers(HttpMethod.PATCH, "/v1/content/*/approve").hasRole("APROVADOR")
                        .requestMatchers(HttpMethod.PATCH, "/v1/content/*/reject").hasRole("APROVADOR")
                        .requestMatchers("/v1/content/pending").hasAnyRole("REVISOR", "APROVADOR")

                        // === Quiz - só Escritor cria, só Estudante realiza (UC11) ===
                        .requestMatchers(HttpMethod.POST, "/v1/quizzes").hasRole("ESCRITOR")
                        .requestMatchers(HttpMethod.POST, "/v1/quiz-attempts").hasRole("ESTUDANTE")

                        // === Fórum - moderação só Admin/Superadmin (UC15) ===
                        .requestMatchers(HttpMethod.DELETE, "/v1/forum-threads/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/posts/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/comments/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        // Qualquer outro endpoint requer autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
