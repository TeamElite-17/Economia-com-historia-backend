package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.AuthResponse;
import com.isptec.economiahistoriaapi.dto.LoginRequest;
import com.isptec.economiahistoriaapi.dto.RegisterRequest;
import com.isptec.economiahistoriaapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "01. Autenticação", description = "UC01 Login · UC02 Registo de conta")
public class AuthController {

    private final AuthService authService;

    /**
     * UC01 — Efetuar Login
     * Disponível para todos os atores.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * UC02 — Registar Conta
     * Disponível para Estudante e Escritor (registo público cria ESTUDANTE por defeito).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }
}
