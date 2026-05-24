package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.CommentDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Comment;
import com.isptec.economiahistoriaapi.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
@Tag(name = "10. Fórum - Comentários", description = "UC14 Publicar comentário · UC15 Moderação")
public class CommentController {

    private final CommentService commentService;

    /**
     * UC14 — Listar comentários de um post (todos os autenticados)
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable String postId) {
        List<CommentDTO> comments = commentService.getCommentsByPost(postId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    /**
     * Obter comentário por ID
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable String commentId) {
        return commentService.getCommentById(commentId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comentário não encontrado com ID: " + commentId));
    }

    /**
     * UC14 — Publicar comentário (todos os atores autenticados)
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO dto) {
        Comment comment = convertToEntity(dto);
        return new ResponseEntity<>(convertToDTO(commentService.createComment(comment)), HttpStatus.CREATED);
    }

    /**
     * Editar comentário
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentDTO dto) {
        commentService.getCommentById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comentário não encontrado com ID: " + commentId));
        Comment updated = convertToEntity(dto);
        updated.setCommentId(commentId);
        return ResponseEntity.ok(convertToDTO(commentService.updateComment(updated)));
    }

    /**
     * UC15 — Moderar fórum: eliminar comentário inapropriado (Admin, Superadmin)
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        if (!commentService.getCommentById(commentId).isPresent()) {
            throw new ResourceNotFoundException("Comentário não encontrado com ID: " + commentId);
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // ========== Conversão ==========

    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .commentedAt(comment.getCommentedAt() != null ? comment.getCommentedAt().toString() : null)
                .postId(comment.getPost() != null ? comment.getPost().getPostId() : null)
                .userId(comment.getAuthor() != null ? comment.getAuthor().getUserId() : null)
                .build();
    }

    private Comment convertToEntity(CommentDTO dto) {
        return Comment.builder()
                .commentId(dto.getCommentId())
                .content(dto.getContent())
                .build();
    }
}
