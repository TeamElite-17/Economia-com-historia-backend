package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.ContentStatsDTO;
import com.isptec.economiahistoriaapi.service.ContentStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/v1/content-items")
@RequiredArgsConstructor
@Tag(name = "13. Estatísticas de Conteúdo", description = "Visualizações, partilhas, gostos e comentários por conteúdo")
public class ContentStatsController {

    private final ContentStatsService statsService;

    /**
     * Obtém todas as estatísticas de um conteúdo.
     * Se o utilizador estiver autenticado, indica se já deu gosto.
     */
    @GetMapping("/{id}/stats")
    @Operation(summary = "Estatísticas de um conteúdo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ContentStatsDTO> getStats(@PathVariable String id, Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(statsService.getStats(id, email));
    }

    /**
     * Regista uma visualização do conteúdo.
     */
    @PostMapping("/{id}/view")
    @Operation(summary = "Registar visualização", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> registerView(@PathVariable String id) {
        statsService.registerView(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Regista uma partilha do conteúdo.
     */
    @PostMapping("/{id}/share")
    @Operation(summary = "Registar partilha", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> registerShare(@PathVariable String id) {
        statsService.registerShare(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Toggle de gosto: dá gosto se ainda não deu, remove se já deu.
     * Retorna {"liked": true/false}.
     */
    @PostMapping("/{id}/like")
    @Operation(summary = "Toggle gosto (like/unlike)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable String id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        boolean liked = statsService.toggleLike(id, principal.getName());
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
