package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.TopicDTO;
import com.isptec.economiahistoriaapi.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/topics")
@RequiredArgsConstructor
@Tag(name = "12. Tópicos", description = "Tópicos de discussão independentes de módulos")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "Listar todos os tópicos")
    public ResponseEntity<List<TopicDTO>> findAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter tópico por ID")
    public ResponseEntity<TopicDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Criar tópico", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicDTO> create(@Valid @RequestBody TopicDTO dto, Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return new ResponseEntity<>(topicService.create(dto, email), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Atualizar tópico", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicDTO> update(@PathVariable String id,
                                            @Valid @RequestBody TopicDTO dto) {
        return ResponseEntity.ok(topicService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Apagar tópico", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable String id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
