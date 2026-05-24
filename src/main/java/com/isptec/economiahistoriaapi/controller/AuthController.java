package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.AuthResponse;
import com.isptec.economiahistoriaapi.dto.LoginRequest;
import com.isptec.economiahistoriaapi.dto.RegisterRequest;
import com.isptec.economiahistoriaapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "01. Autenticação", description = "UC01 Login · UC02 Registo de conta · UC03 Logout")
public class AuthController {

    private final AuthService authService;

    /**
     * UC01 — Efetuar Login
     * Disponível para todos os atores.
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica o utilizador e devolve um token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * UC02 — Registar Conta
     * Disponível para Estudante e Escritor (registo público cria ESTUDANTE por defeito).
     */
    @PostMapping("/register")
    @Operation(summary = "Registar conta", description = "Cria uma nova conta de Estudante")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    /**
     * UC03 — Logout
     * Invalida o token JWT atual adicionando-o à blacklist.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalida o token JWT (blacklist). Requer Authorization: Bearer <token>",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.noContent().build();
    }
}
