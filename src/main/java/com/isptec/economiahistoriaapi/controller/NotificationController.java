package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Notification;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "11. Notificações", description = "Listar, marcar como lida e criar notificações")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private static final SimpleDateFormat ISO_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // ─── DTO de resposta ──────────────────────────────────────────────────────

    @Data
    static class NotificationDTO {
        String notificationId;
        String message;
        boolean read;
        String createdAt;
        String userId;
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.notificationId = n.getNotificationId();
        dto.message = n.getMessage();
        dto.read = n.isRead();
        dto.createdAt = n.getCreatedAt() != null ? ISO_FMT.format(n.getCreatedAt()) : null;
        dto.userId = n.getUser() != null ? n.getUser().getUserId() : null;
        return dto;
    }

    // ─── Endpoints ────────────────────────────────────────────────────────────

    /** Listar todas as notificações do utilizador autenticado */
    @GetMapping("/my")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        String email = getEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        List<NotificationDTO> list = notificationService.getNotificationsByUser(user.getUserId())
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** Número de notificações não lidas */
    @GetMapping("/my/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        String email = getEmail();
        if (email == null) return ResponseEntity.ok(Map.of("count", 0L));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(Map.of("count", 0L));
        long count = notificationService.getUnreadNotificationsByUser(user.getUserId()).size();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /** Marcar uma notificação como lida */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable String notificationId) {
        Notification n = notificationService.getNotificationById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        n.setRead(true);
        return ResponseEntity.ok(toDTO(notificationService.updateNotification(n)));
    }

    /** Marcar todas as notificações do utilizador como lidas */
    @PatchMapping("/my/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        String email = getEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        notificationService.getUnreadNotificationsByUser(user.getUserId()).forEach(n -> {
            n.setRead(true);
            notificationService.updateNotification(n);
        });
        return ResponseEntity.noContent().build();
    }

    /** Eliminar uma notificação */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String notificationId) {
        notificationService.getNotificationById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    private String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
