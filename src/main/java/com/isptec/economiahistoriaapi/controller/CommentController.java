package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.CommentDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Comment;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.model.Notification;
import com.isptec.economiahistoriaapi.model.Post;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.ContentItemRepository;
import com.isptec.economiahistoriaapi.repository.PostRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.CommentService;
import com.isptec.economiahistoriaapi.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
@Tag(name = "10. Fórum - Comentários", description = "UC14 Publicar comentário · UC15 Moderação")
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ContentItemRepository contentItemRepository;
    private final com.isptec.economiahistoriaapi.repository.CommentRepository commentRepository;
    private final NotificationService notificationService;

    /**
     * UC14 — Listar comentários de um post do fórum
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable String postId) {
        List<CommentDTO> comments = commentService.getCommentsByPost(postId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    /**
     * Listar sub-respostas (filhos) de um comentário
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDTO>> getRepliesByComment(@PathVariable String commentId) {
        List<CommentDTO> replies = commentRepository.findByParentCommentId(commentId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(replies);
    }

    /**
     * Listar comentários de um conteúdo didático
     */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByContent(@PathVariable String contentId) {
        List<CommentDTO> comments = commentService.getCommentsByContentItem(contentId)
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
     * UC14 — Publicar comentário (todos os atores autenticados).
     * Aceita postId (comentário de fórum) ou contentItemId (comentário de conteúdo).
     * O userId pode vir no DTO ou ser inferido do utilizador autenticado.
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO dto) {
        // Se userId não vier no DTO, usa o utilizador autenticado
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            String email = getLoggedInEmail();
            if (email != null) {
                userRepository.findByEmail(email)
                        .ifPresent(u -> dto.setUserId(u.getUserId()));
            }
        }
        Comment comment = convertToEntity(dto);
        Comment saved = commentService.createComment(comment);

        // ── Notificação (eager loading via queries dedicadas) ─────────────────
        try {
            String commenterName = "Alguém";
            if (dto.getUserId() != null) {
                commenterName = userRepository.findById(dto.getUserId())
                        .map(u -> u.getName()).orElse("Alguém");
            }

            User target = null;
            String msg = null;

            if (dto.getParentCommentId() != null && !dto.getParentCommentId().isBlank()) {
                // Resposta a um comentário — notifica o autor do comentário pai
                target = commentRepository.findByIdWithAuthor(dto.getParentCommentId())
                        .map(c -> c.getAuthor()).orElse(null);
                msg = commenterName + " respondeu ao teu comentário";
            } else if (dto.getPostId() != null && !dto.getPostId().isBlank()) {
                // Comentário num post do fórum — notifica o autor do post
                target = postRepository.findByIdWithAuthor(dto.getPostId())
                        .map(p -> p.getAuthor()).orElse(null);
                msg = commenterName + " comentou na tua resposta";
            } else if (dto.getContentItemId() != null && !dto.getContentItemId().isBlank()) {
                // Comentário num conteúdo didático — notifica o autor do conteúdo
                String authorId = contentItemRepository.findById(dto.getContentItemId())
                        .map(ci -> ci.getAuthorId()).orElse(null);
                if (authorId != null) {
                    target = userRepository.findById(authorId).orElse(null);
                }
                msg = commenterName + " comentou no teu conteúdo";
            }

            // Só notifica se o alvo existe e é diferente de quem comentou
            if (target != null && !target.getUserId().equals(dto.getUserId())) {
                notificationService.createNotification(
                    Notification.builder().user(target).message(msg).read(false).build()
                );
            }
        } catch (Exception e) {
            System.err.println("[NotificationError] " + e.getMessage());
        }


        return new ResponseEntity<>(convertToDTO(saved), HttpStatus.CREATED);

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
        String avatarUrl = null;
        String authorName = null;
        if (comment.getAuthor() != null) {
            authorName = comment.getAuthor().getName();
            // gera avatar a partir do nome se não existir URL próprio
            avatarUrl = "https://ui-avatars.com/api/?name="
                    + java.net.URLEncoder.encode(authorName, java.nio.charset.StandardCharsets.UTF_8)
                    + "&background=7B1D2D&color=fff&size=200";
        }

        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .commentedAt(comment.getCommentedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(comment.getCommentedAt()) : null)
                .postId(comment.getPost() != null ? comment.getPost().getPostId() : null)
                .contentItemId(comment.getContentItem() != null ? comment.getContentItem().getContentId() : null)
                .userId(comment.getAuthor() != null ? comment.getAuthor().getUserId() : null)
                .userName(authorName)
                .userAvatar(avatarUrl)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .build();
    }

    private Comment convertToEntity(CommentDTO dto) {
        Comment.CommentBuilder builder = Comment.builder()
                .commentId(dto.getCommentId())
                .content(dto.getContent());

        // Liga ao post do fórum (se fornecido)
        if (dto.getPostId() != null && !dto.getPostId().isBlank()) {
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Post não encontrado com ID: " + dto.getPostId()));
            builder.post(post);
        }

        // Liga ao conteúdo didático (se fornecido)
        if (dto.getContentItemId() != null && !dto.getContentItemId().isBlank()) {
            ContentItem contentItem = contentItemRepository.findById(dto.getContentItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Conteúdo não encontrado com ID: " + dto.getContentItemId()));
            builder.contentItem(contentItem);
        }

        // Liga ao utilizador autor (se fornecido)
        if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilizador não encontrado com ID: " + dto.getUserId()));
            builder.author(user);
        }

        // Liga ao comentário pai (sub-resposta aninhada)
        if (dto.getParentCommentId() != null && !dto.getParentCommentId().isBlank()) {
            Comment parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Comentário pai não encontrado com ID: " + dto.getParentCommentId()));
            builder.parentComment(parent);
        }

        return builder.build();
    }

    private String getLoggedInEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
