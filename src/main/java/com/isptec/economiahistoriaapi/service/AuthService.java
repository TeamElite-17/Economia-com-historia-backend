package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.config.JwtUtil;
import com.isptec.economiahistoriaapi.dto.AuthResponse;
import com.isptec.economiahistoriaapi.dto.LoginRequest;
import com.isptec.economiahistoriaapi.dto.RegisterRequest;
import com.isptec.economiahistoriaapi.enums.UserRole;
import com.isptec.economiahistoriaapi.exception.BadRequestException;
import com.isptec.economiahistoriaapi.exception.ConflictException;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.dto.ForgotPasswordRequest;
import com.isptec.economiahistoriaapi.dto.ResetPasswordRequest;
import com.isptec.economiahistoriaapi.service.TokenBlacklistService;
import com.isptec.economiahistoriaapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailService emailService;

    /**
     * UC01 — Efetuar Login
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return buildResponse(user, token);
    }

    /**
     * UC02 — Registar Conta (apenas ESTUDANTE por registo público)
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email já se encontra registado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ESTUDANTE)
                .preferredLanguage(request.getPreferredLanguage())
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return buildResponse(savedUser, token);
    }

    /**
     * UC03 — Logout
     * Invalida o token JWT adicionando-o à blacklist.
     */
    public void logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            tokenBlacklistService.invalidate(token);
        }
    }

    private AuthResponse buildResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Se o email existir, será enviado um link de recuperação."));

        String token = jwtUtil.generatePasswordResetToken(user.getEmail());
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!jwtUtil.isTokenValid(request.getToken())) {
            throw new BadRequestException("Token inválido ou expirado");
        }

        String role = jwtUtil.extractRole(request.getToken());
        if (!"RESET_PASSWORD".equals(role)) {
            throw new BadRequestException("Token inválido");
        }

        String email = jwtUtil.extractEmail(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilizador não encontrado"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Opcionalmente invalidar o token de reset, se mantivermos estado de tokens invalidados
        tokenBlacklistService.invalidate(request.getToken());
    }
}
