package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.UserDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Obter detalhes de um utilizador
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        return userService.getUserById(userId)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilizador não encontrado com ID: " + userId));
    }
    
    /**
     * Obter utilizador por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilizador não encontrado com email: " + email));
    }
    
    /**
     * Listar todos os utilizadores
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * Criar novo utilizador
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(convertToDTO(savedUser), HttpStatus.CREATED);
    }
    
    /**
     * Atualizar utilizador
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDTO userDTO) {
        
        User existingUser = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilizador não encontrado com ID: " + userId));
        
        User updatedUser = convertToEntity(userDTO);
        updatedUser.setUserId(userId);
        updatedUser.setRegistrationDate(existingUser.getRegistrationDate());
        User savedUser = userService.updateUser(updatedUser);
        
        return new ResponseEntity<>(convertToDTO(savedUser), HttpStatus.OK);
    }
    
    /**
     * Deletar utilizador
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Utilizador não encontrado com ID: " + userId);
        }
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // ========== Métodos de Conversão ==========
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .preferredLanguage(user.getPreferredLanguage())
                .registrationDate(user.getRegistrationDate() != null ? 
                        user.getRegistrationDate().toString() : null)
                .build();
    }
    
    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .passwordHash(userDTO.getPassword())
                .preferredLanguage(userDTO.getPreferredLanguage())
                .build();
    }
}
