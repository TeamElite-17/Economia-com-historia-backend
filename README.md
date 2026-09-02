# Economia com História - Backend

API REST do projeto **Economia com História**, construída com **Spring Boot** para suportar autenticação, gestão de conteúdo educativo e recursos de comunidade (fórum, comentários, quizzes e notificações).

## Objetivo do projeto

Este backend centraliza:
- autenticação e autorização com JWT;
- gestão de utilizadores e perfis;
- conteúdos educativos (categorias, tópicos, posts, itens de conteúdo);
- interação social (comentários, fórum, likes, notificações);
- quizzes e tentativas;
- upload de ficheiros (imagem/áudio/vídeo).

## Stack tecnológica

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA + Hibernate
- Spring Security + JWT
- MySQL
- Spring Mail
- Springdoc OpenAPI (Swagger UI)
- Maven Wrapper (`mvnw`)

## Como executar localmente

### 1) Pré-requisitos
- Java 17
- MySQL em execução

### 2) Configuração (variáveis de ambiente)
As principais configurações estão em `src/main/resources/application.properties` e podem ser sobrescritas por variáveis de ambiente:

- `SERVER_PORT` (default: `8080`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JPA_HIBERNATE_DDL` (ex.: `update`)
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `FILE_UPLOAD_DIR`
- `MAX_FILE_SIZE`

### 3) Executar a aplicação
```bash
./mvnw spring-boot:run
```

A API sobe, por padrão, em:
- `http://localhost:8080/api`

Documentação Swagger:
- `http://localhost:8080/api/swagger-ui.html`

## Testes

```bash
./mvnw test
```

## Estrutura do projeto

```text
src/main/java/com/isptec/economiahistoriaapi
├── config/         # Segurança, JWT, CORS, Swagger
├── controller/     # Endpoints REST
├── dto/            # Objetos de transporte de dados
├── enums/          # Enumerações de domínio
├── exception/      # Exceções customizadas e handler global
├── model/          # Entidades JPA
├── repository/     # Acesso a dados (Spring Data JPA)
└── service/        # Regras de negócio e casos de uso

src/main/resources
└── application.properties  # Configuração da aplicação

src/test/java
└── ... # Testes

uploads/
├── images/
└── audios/
```

## Principais módulos funcionais (resumo)

- **Auth/User**: login, registo, gestão de perfis e permissões.
- **Conteúdo**: categorias, tópicos, posts, itens e estatísticas de conteúdo.
- **Comunidade**: comentários, fórum e notificações.
- **Avaliação**: quizzes, perguntas, opções de resposta e tentativas.
- **Media**: upload e armazenamento de ficheiros.

