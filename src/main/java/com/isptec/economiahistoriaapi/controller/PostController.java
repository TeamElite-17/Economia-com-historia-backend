package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.PostDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Post;
import com.isptec.economiahistoriaapi.model.PostLike;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.PostLikeRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.ForumThreadService;
import com.isptec.economiahistoriaapi.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
@Tag(name = "09. Fórum - Posts", description = "UC13 Posts de tópicos · UC14 Respostas · UC15 Moderação")
public class PostController {

    private final PostService postService;
    private final ForumThreadService forumThreadService;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    /** Listar posts de uma thread */
    @GetMapping("/thread/{threadId}")
    public ResponseEntity<List<PostDTO>> getPostsByThread(@PathVariable String threadId) {
        String currentUserId = resolveCurrentUserId();
        return ResponseEntity.ok(postService.getPostsByThread(threadId)
                .stream().map(p -> convertToDTO(p, currentUserId)).collect(Collectors.toList()));
    }

    /** Obter post por ID */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable String postId) {
        String currentUserId = resolveCurrentUserId();
        return postService.getPostById(postId)
                .map(p -> convertToDTO(p, currentUserId))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado com ID: " + postId));
    }

    /**
     * Criar post / resposta.
     * O userId pode vir no DTO ou ser inferido do JWT.
     */
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO dto) {
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            String email = getLoggedInEmail();
            if (email != null) {
                userRepository.findByEmail(email).ifPresent(u -> dto.setUserId(u.getUserId()));
            }
        }
        Post post = convertToEntity(dto);
        System.out.println("DEBUG - Creating Post: threadId=" + dto.getThreadId() + ", hasForumThread=" + (post.getForumThread() != null));
        Post saved = postService.createPost(post);
        System.out.println("DEBUG - Saved Post: postId=" + saved.getPostId());
        String currentUserId = dto.getUserId();
        return new ResponseEntity<>(convertToDTO(saved, currentUserId), HttpStatus.CREATED);
    }

    /** Editar post */
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String postId, @Valid @RequestBody PostDTO dto) {
        postService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado com ID: " + postId));
        Post updated = convertToEntity(dto);
        updated.setPostId(postId);
        String currentUserId = resolveCurrentUserId();
        return ResponseEntity.ok(convertToDTO(postService.updatePost(updated), currentUserId));
    }

    /** Eliminar post (Admin/Superadmin) */
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        if (!postService.getPostById(postId).isPresent()) {
            throw new ResourceNotFoundException("Post não encontrado com ID: " + postId);
        }
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggle like num post do fórum.
     * Requer autenticação.
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable String postId) {
        String email = getLoggedInEmail();
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + postId));

        Optional<PostLike> existing = postLikeRepository.findByPostIdAndUserId(postId, user.getUserId());
        boolean liked;
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            liked = false;
        } else {
            PostLike newLike = PostLike.builder().post(post).user(user).build();
            postLikeRepository.save(newLike);
            liked = true;
        }

        long likeCount = postLikeRepository.countByPostId(postId);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
    }

    /**
     * Obter contagem de likes e estado do like para um post.
     */
    @GetMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Object>> getLikes(@PathVariable String postId) {
        String userId = resolveCurrentUserId();
        long likeCount = postLikeRepository.countByPostId(postId);
        boolean liked = userId != null && postLikeRepository.existsByPostIdAndUserId(postId, userId);
        return ResponseEntity.ok(Map.of("likeCount", likeCount, "liked", liked));
    }

    // ========== Conversão ==========

    private PostDTO convertToDTO(Post post, String currentUserId) {
        String authorName = null;
        String authorAvatar = null;
        if (post.getAuthor() != null) {
            authorName = post.getAuthor().getName();
            authorAvatar = "https://ui-avatars.com/api/?name="
                    + URLEncoder.encode(authorName, StandardCharsets.UTF_8)
                    + "&background=7B1D2D&color=fff&size=200";
        }

        long likeCount = postLikeRepository.countByPostId(post.getPostId());
        boolean liked = currentUserId != null
                && postLikeRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);

        return PostDTO.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .postedAt(post.getPostedAt() != null ? post.getPostedAt().toString() : null)
                .threadId(post.getForumThread() != null ? post.getForumThread().getThreadId() : null)
                .userId(post.getAuthor() != null ? post.getAuthor().getUserId() : null)
                .userName(authorName)
                .userAvatar(authorAvatar)
                .likeCount(likeCount)
                .likedByCurrentUser(liked)
                .build();
    }

    private Post convertToEntity(PostDTO dto) {
        Post post = Post.builder()
                // Não usar IDs de draft (ex: "draft-1234") - deixar Hibernate gerar UUID
                .postId(dto.getPostId() != null && !dto.getPostId().startsWith("draft-") ? dto.getPostId() : null)
                .content(dto.getContent())
                .build();

        if (dto.getThreadId() != null && !dto.getThreadId().isBlank()) {
            forumThreadService.getThreadById(dto.getThreadId())
                    .ifPresent(post::setForumThread);
        }
        if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
            userRepository.findById(dto.getUserId()).ifPresent(post::setAuthor);
        }
        return post;
    }

    private String resolveCurrentUserId() {
        String email = getLoggedInEmail();
        if (email == null) return null;
        return userRepository.findByEmail(email).map(User::getUserId).orElse(null);
    }

    private String getLoggedInEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
