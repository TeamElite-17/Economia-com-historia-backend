package com.isptec.economiahistoriaapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Servidor de Desenvolvimento Local")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, jwtSecurityScheme())
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Economia e História — API REST")
                .description("""
                        API da plataforma educacional de **Economia e História** do ISPTEC.
                        
                        ## Autenticação
                        Esta API utiliza **JWT Bearer Token**. Para autenticar:
                        1. Regista uma conta em `POST /v1/auth/register`
                        2. Faz login em `POST /v1/auth/login` e copia o token recebido
                        3. Clica em **Authorize** (cadeado) e cola o token no campo `bearerAuth`
                        
                        ## Roles do Sistema
                        | Role | Descrição |
                        |------|-----------|
                        | `SUPERADMIN` | Controlo total da plataforma |
                        | `ADMIN` | Gestão operacional e moderação |
                        | `APROVADOR` | Publica/rejeita conteúdos |
                        | `REVISOR` | Audita e valida conteúdos |
                        | `ESCRITOR` | Cria rascunhos de conteúdo |
                        | `ESTUDANTE` | Consome conteúdos e realiza quizzes |
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("ISPTEC — Engenharia de Software")
                        .email("engsoft@isptec.co.ao")
                )
                .license(new License()
                        .name("Uso Académico")
                        .url("https://www.isptec.co.ao")
                );
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Introduza o token JWT obtido em POST /v1/auth/login");
    }
}
