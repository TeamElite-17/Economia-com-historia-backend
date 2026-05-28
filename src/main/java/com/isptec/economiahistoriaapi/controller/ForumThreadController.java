package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.ForumThreadDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.ForumThread;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.ForumModuleRepository;
import com.isptec.economiahistoriaapi.repository.PostRepository;
import com.isptec.economiahistoriaapi.repository.TopicRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.ForumThreadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/forum-threads")
@RequiredArgsConstructor
@Tag(name = "08. Fórum - Tópicos", description = "UC13 Criar tópico de discussão")
public class ForumThreadController {

    private final ForumThreadService forumThreadService;
    private final TopicRepository topicRepository;
    private final ForumModuleRepository forumModuleRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /** Obter detalhe de uma thread */
    @GetMapping("/{threadId}")
    public ResponseEntity<ForumThreadDTO> getThreadById(@PathVariable String threadId) {
        return forumThreadService.getThreadById(threadId)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread do fórum não encontrada com ID: " + threadId));
    }

    /** Listar todas as threads */
    @GetMapping
    public ResponseEntity<List<ForumThreadDTO>> getAllThreads() {
        List<ForumThreadDTO> threads = forumThreadService.getAllThreads()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(threads, HttpStatus.OK);
    }

    /** Listar threads por módulo */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ForumThreadDTO>> getThreadsByModule(@PathVariable String moduleId) {
        return new ResponseEntity<>(forumThreadService.getThreadsByModule(moduleId)
                .stream().map(this::convertToDTO).collect(Collectors.toList()), HttpStatus.OK);
    }

    /** Listar threads por tópico */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ForumThreadDTO>> getThreadsByTopic(@PathVariable String topicId) {
        return new ResponseEntity<>(forumThreadService.getThreadsByTopic(topicId)
                .stream().map(this::convertToDTO).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Criar nova thread.
     * O utilizador criador pode vir no DTO (createdByUserId) ou ser inferido do JWT.
     */
    @PostMapping
    public ResponseEntity<ForumThreadDTO> createThread(@Valid @RequestBody ForumThreadDTO threadDTO) {
        ForumThread thread = convertToEntity(threadDTO);
        ForumThread saved = forumThreadService.createThread(thread);
        return new ResponseEntity<>(convertToDTO(saved), HttpStatus.CREATED);
    }

    /** Atualizar thread */
    @PutMapping("/{threadId}")
    public ResponseEntity<ForumThreadDTO> updateThread(
            @PathVariable String threadId,
            @Valid @RequestBody ForumThreadDTO threadDTO) {
        ForumThread existing = forumThreadService.getThreadById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread do fórum não encontrada com ID: " + threadId));
        ForumThread updated = convertToEntity(threadDTO);
        updated.setThreadId(threadId);
        updated.setCreatedAt(existing.getCreatedAt());
        if (updated.getCreatedByUser() == null) {
            updated.setCreatedByUser(existing.getCreatedByUser());
        }
        return new ResponseEntity<>(convertToDTO(forumThreadService.updateThread(updated)), HttpStatus.OK);
    }

    /** Deletar thread */
    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteThread(@PathVariable String threadId) {
        if (!forumThreadService.getThreadById(threadId).isPresent()) {
            throw new ResourceNotFoundException("Thread do fórum não encontrada com ID: " + threadId);
        }
        forumThreadService.deleteThread(threadId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== Conversão ==========

    private ForumThreadDTO convertToDTO(ForumThread thread) {
        String creatorId = null;
        String creatorName = null;
        String creatorAvatar = null;
        if (thread.getCreatedByUser() != null) {
            User creator = thread.getCreatedByUser();
            creatorId = creator.getUserId();
            creatorName = creator.getName();
            creatorAvatar = "https://ui-avatars.com/api/?name="
                    + URLEncoder.encode(creatorName, StandardCharsets.UTF_8)
                    + "&background=7B1D2D&color=fff&size=200";
        }

        int postCount = postRepository.countByForumThreadId(thread.getThreadId()).intValue();

        return ForumThreadDTO.builder()
                .threadId(thread.getThreadId())
                .title(thread.getTitle())
                .createdAt(thread.getCreatedAt() != null ? thread.getCreatedAt().toString() : null)
                .forumModuleId(thread.getForumModule() != null ? thread.getForumModule().getModuleId() : null)
                .topicId(thread.getTopic() != null ? thread.getTopic().getTopicId() : null)
                .createdByUserId(creatorId)
                .createdByUserName(creatorName)
                .createdByUserAvatar(creatorAvatar)
                .postCount(postCount)
                .build();
    }

    private ForumThread convertToEntity(ForumThreadDTO dto) {
        ForumThread thread = ForumThread.builder()
                .title(dto.getTitle())
                .build();

        if (dto.getForumModuleId() != null) {
            forumModuleRepository.findById(dto.getForumModuleId())
                    .ifPresent(thread::setForumModule);
        }

        if (dto.getTopicId() != null) {
            topicRepository.findById(dto.getTopicId())
                    .ifPresent(thread::setTopic);
        }

        // Define o utilizador criador: do DTO ou do JWT
        String userId = dto.getCreatedByUserId();
        if (userId == null || userId.isBlank()) {
            String email = getLoggedInEmail();
            if (email != null) {
                userId = userRepository.findByEmail(email)
                        .map(User::getUserId)
                        .orElse(null);
            }
        }
        if (userId != null) {
            userRepository.findById(userId).ifPresent(thread::setCreatedByUser);
        }

        return thread;
    }

    private String getLoggedInEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
