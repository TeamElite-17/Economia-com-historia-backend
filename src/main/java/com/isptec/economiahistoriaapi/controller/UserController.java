package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.UserDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.model.UserCollection;
import com.isptec.economiahistoriaapi.repository.UserCollectionRepository;
import com.isptec.economiahistoriaapi.repository.QuizAttemptRepository;
import com.isptec.economiahistoriaapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserCollectionRepository userCollectionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    
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
        int historyCount = userCollectionRepository.findByUserIdAndItemType(user.getUserId(), "HISTORY").size();
        int subsCount = userCollectionRepository.findByUserIdAndItemType(user.getUserId(), "SUBSCRIPTION").size();
        int quizCount = quizAttemptRepository.findByUserId(user.getUserId()).size();
        int subscribersCount = userCollectionRepository.findByItemTypeAndItemId("SUBSCRIPTION", user.getUserId()).size();

        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .preferredLanguage(user.getPreferredLanguage())
                .registrationDate(user.getRegistrationDate() != null ? 
                        user.getRegistrationDate().toString() : null)
                .watchHistoryCount(historyCount)
                .completedQuizzesCount(quizCount)
                .subscriptionsCount(subsCount)
                .subscribersCount(subscribersCount)
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

    // ========== Coleções do Utilizador ==========

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(toCollectionResponse(userCollectionRepository.findByUserIdAndItemType(userId, "HISTORY")));
    }

    @PostMapping("/{userId}/history")
    public ResponseEntity<Map<String, Object>> addHistory(@PathVariable String userId, @RequestBody Map<String, String> body) {
        return saveCollectionItem(userId, "HISTORY", body.get("itemId"));
    }

    @GetMapping("/{userId}/saved")
    public ResponseEntity<List<Map<String, Object>>> getSaved(@PathVariable String userId) {
        return ResponseEntity.ok(toCollectionResponse(userCollectionRepository.findByUserIdAndItemType(userId, "SAVED")));
    }

    @PostMapping("/{userId}/saved")
    public ResponseEntity<Map<String, Object>> addSaved(@PathVariable String userId, @RequestBody Map<String, String> body) {
        return saveCollectionItem(userId, "SAVED", body.get("itemId"));
    }

    @DeleteMapping("/{userId}/saved/{itemId}")
    public ResponseEntity<Void> removeSaved(@PathVariable String userId, @PathVariable String itemId) {
        userCollectionRepository.deleteByUserIdAndItemTypeAndItemId(userId, "SAVED", itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/subscriptions")
    public ResponseEntity<List<Map<String, Object>>> getSubscriptions(@PathVariable String userId) {
        return ResponseEntity.ok(toCollectionResponse(userCollectionRepository.findByUserIdAndItemType(userId, "SUBSCRIPTION")));
    }

    @PostMapping("/{userId}/subscriptions")
    public ResponseEntity<Map<String, Object>> addSubscription(@PathVariable String userId, @RequestBody Map<String, String> body) {
        return saveCollectionItem(userId, "SUBSCRIPTION", body.get("itemId"));
    }

    @DeleteMapping("/{userId}/subscriptions/{itemId}")
    public ResponseEntity<Void> removeSubscription(@PathVariable String userId, @PathVariable String itemId) {
        userCollectionRepository.deleteByUserIdAndItemTypeAndItemId(userId, "SUBSCRIPTION", itemId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, Object>> saveCollectionItem(String userId, String itemType, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UserCollection item = userCollectionRepository
                .findByUserIdAndItemTypeAndItemId(userId, itemType, itemId)
                .orElseGet(() -> userCollectionRepository.save(
                        UserCollection.builder().userId(userId).itemType(itemType).itemId(itemId).build()));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("collectionId", item.getCollectionId(), "userId", userId, "itemType", itemType, "itemId", itemId));
    }

    private List<Map<String, Object>> toCollectionResponse(List<UserCollection> items) {
        return items.stream().map(i -> {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("collectionId", i.getCollectionId());
            m.put("userId", i.getUserId());
            m.put("itemType", i.getItemType());
            m.put("itemId", i.getItemId());
            m.put("notificationPref", i.getNotificationPref() != null ? i.getNotificationPref() : "ALL");
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * PATCH /v1/users/{userId}/subscriptions/{authorId}/notification-pref
     * Altera a preferência de notificação de uma subscrição.
     * Body: { "notificationPref": "ALL" | "NONE" }
     */
    @PatchMapping("/{userId}/subscriptions/{authorId}/notification-pref")
    public ResponseEntity<Map<String, Object>> updateSubscriptionNotificationPref(
            @PathVariable String userId,
            @PathVariable String authorId,
            @RequestBody Map<String, String> body) {
        String pref = body.get("notificationPref");
        if (pref == null || (!pref.equals("ALL") && !pref.equals("NONE"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "notificationPref deve ser ALL ou NONE"));
        }
        UserCollection sub = userCollectionRepository
                .findByUserIdAndItemTypeAndItemId(userId, "SUBSCRIPTION", authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscrição não encontrada"));
        sub.setNotificationPref(pref);
        userCollectionRepository.save(sub);
        return ResponseEntity.ok(Map.of(
                "collectionId", sub.getCollectionId(),
                "userId", userId,
                "itemId", authorId,
                "notificationPref", pref
        ));
    }
}
