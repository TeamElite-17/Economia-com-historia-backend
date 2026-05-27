package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.RoleUpdateRequest;
import com.isptec.economiahistoriaapi.dto.UserDTO;
import com.isptec.economiahistoriaapi.enums.UserRole;
import com.isptec.economiahistoriaapi.exception.BadRequestException;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "03. Administração", description = "UC04 Gerir admins · UC05 Promover Revisor · UC06 Gerir utilizadores")
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * UC04/UC06 — Listar todos os utilizadores (Admin, Superadmin)
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * UC06 — Obter utilizador por ID (Admin, Superadmin)
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        return userService.getUserById(userId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilizador não encontrado com ID: " + userId));
    }

    /**
     * UC04 — Criar conta administrativa (Admin, Aprovador) — apenas Superadmin
     */
    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UserDTO> createAdminUser(@Valid @RequestBody UserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BadRequestException("A palavra-passe é obrigatória");
        }
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.valueOf(dto.getRole()))
                .preferredLanguage(dto.getPreferredLanguage())
                .build();
        return new ResponseEntity<>(convertToDTO(userService.createUser(user)), HttpStatus.CREATED);
    }

    /**
     * UC05 — Promover utilizador a Revisor; UC04 — alterar role (apenas Superadmin)
     */
    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable String userId,
            @Valid @RequestBody RoleUpdateRequest request) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilizador não encontrado com ID: " + userId));

        try {
            user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role inválido: " + request.getRole());
        }

        return ResponseEntity.ok(convertToDTO(userService.updateUser(user)));
    }

    /**
     * UC04/UC06 — Eliminar/suspender conta (Admin, Superadmin)
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new ResourceNotFoundException("Utilizador não encontrado com ID: " + userId);
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ========== Conversão ==========

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .preferredLanguage(user.getPreferredLanguage())
                .registrationDate(user.getRegistrationDate() != null ?
                        user.getRegistrationDate().toString() : null)
                .build();
    }
}
