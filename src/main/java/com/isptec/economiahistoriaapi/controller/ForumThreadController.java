package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.ForumThreadDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.ForumThread;
import com.isptec.economiahistoriaapi.service.ForumThreadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/forum-threads")
@RequiredArgsConstructor
@Tag(name = "08. Fórum - Tópicos", description = "UC13 Criar tópico de discussão")
public class ForumThreadController {
    
    private final ForumThreadService forumThreadService;
    
    /**
     * Obter detalhes de uma thread do fórum
     */
    @GetMapping("/{threadId}")
    public ResponseEntity<ForumThreadDTO> getThreadById(@PathVariable String threadId) {
        return forumThreadService.getThreadById(threadId)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread do fórum não encontrada com ID: " + threadId));
    }
    
    /**
     * Listar todas as threads
     */
    @GetMapping
    public ResponseEntity<List<ForumThreadDTO>> getAllThreads() {
        List<ForumThreadDTO> threads = forumThreadService.getAllThreads()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(threads, HttpStatus.OK);
    }
    
    /**
     * Listar threads por módulo
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ForumThreadDTO>> getThreadsByModule(@PathVariable String moduleId) {
        List<ForumThreadDTO> threads = forumThreadService.getThreadsByModule(moduleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(threads, HttpStatus.OK);
    }
    
    /**
     * Criar nova thread
     */
    @PostMapping
    public ResponseEntity<ForumThreadDTO> createThread(@Valid @RequestBody ForumThreadDTO threadDTO) {
        ForumThread thread = convertToEntity(threadDTO);
        ForumThread savedThread = forumThreadService.createThread(thread);
        return new ResponseEntity<>(convertToDTO(savedThread), HttpStatus.CREATED);
    }
    
    /**
     * Atualizar thread
     */
    @PutMapping("/{threadId}")
    public ResponseEntity<ForumThreadDTO> updateThread(
            @PathVariable String threadId,
            @Valid @RequestBody ForumThreadDTO threadDTO) {
        
        ForumThread existingThread = forumThreadService.getThreadById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thread do fórum não encontrada com ID: " + threadId));
        
        ForumThread updatedThread = convertToEntity(threadDTO);
        updatedThread.setThreadId(threadId);
        updatedThread.setCreatedAt(existingThread.getCreatedAt());
        ForumThread savedThread = forumThreadService.updateThread(updatedThread);
        
        return new ResponseEntity<>(convertToDTO(savedThread), HttpStatus.OK);
    }
    
    /**
     * Deletar thread
     */
    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteThread(@PathVariable String threadId) {
        if (!forumThreadService.getThreadById(threadId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Thread do fórum não encontrada com ID: " + threadId);
        }
        forumThreadService.deleteThread(threadId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // ========== Métodos de Conversão ==========
    
    private ForumThreadDTO convertToDTO(ForumThread thread) {
        return ForumThreadDTO.builder()
                .threadId(thread.getThreadId())
                .title(thread.getTitle())
                .createdAt(thread.getCreatedAt() != null ? 
                        thread.getCreatedAt().toString() : null)
                .forumModuleId(thread.getForumModule() != null ? 
                        thread.getForumModule().getModuleId() : null)
                .build();
    }
    
    private ForumThread convertToEntity(ForumThreadDTO threadDTO) {
        return ForumThread.builder()
                .title(threadDTO.getTitle())
                .build();
    }
}
