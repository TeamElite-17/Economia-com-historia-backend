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
    private final CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Ativa CORS no Spring Security filter chain usando o bean CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    // Preflight CORS
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth - público
                        .requestMatchers("/v1/auth/**").permitAll()
                        // Swagger - público
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Actuator - público
                        .requestMatchers("/actuator/**").permitAll()

                        // Categorias, Tópicos, Utilizadores - GET público
                        .requestMatchers(HttpMethod.GET, "/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/topics/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/users/**").permitAll()

                        // Conteúdos - GET público, escrita autenticada
                        .requestMatchers(HttpMethod.GET, "/v1/content-items/**").permitAll()
                        // Visualizações e partilhas podem ser registadas por qualquer um
                        .requestMatchers(HttpMethod.POST, "/v1/content-items/*/view").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/content-items/*/share").permitAll()
                        // Stats podem ser lidas por qualquer um (likedByCurrentUser será null se não autenticado)
                        .requestMatchers(HttpMethod.GET, "/v1/content-items/*/stats").permitAll()
                        // Like/Unlike requer autenticação
                        .requestMatchers(HttpMethod.POST, "/v1/content-items/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/content-items").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/content-items/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/content-items/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/content-items/**").authenticated()

                        // Quiz - GET público, escrita autenticada
                        .requestMatchers(HttpMethod.GET, "/v1/quizzes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/questions/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/quizzes").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/quizzes/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/quizzes/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/questions").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/quiz-attempts").authenticated()

                        // Fórum - GET público, escrita autenticada
                        .requestMatchers(HttpMethod.GET, "/v1/forum-threads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/forum-threads").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/forum-threads/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/posts/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/comments/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        // Perfis - GET público
                        .requestMatchers(HttpMethod.GET, "/v1/profiles/**").permitAll()

                        // Ficheiros multimédia - download público; upload autenticado (roles no controller)
                        .requestMatchers(HttpMethod.GET, "/v1/files/download/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/files/info/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/files/upload/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/files/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        // Admin - apenas ADMIN e SUPERADMIN
                        .requestMatchers("/v1/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")

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
