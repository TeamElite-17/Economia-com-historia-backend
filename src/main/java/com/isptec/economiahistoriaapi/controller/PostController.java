package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.PostDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Post;
import com.isptec.economiahistoriaapi.service.PostService;
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
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
@Tag(name = "09. Fórum - Posts", description = "UC13 Posts de tópicos · UC14 Respostas · UC15 Moderação")
public class PostController {

    private final PostService postService;

    /**
     * UC13/UC14 — Listar posts de uma thread (todos os autenticados)
     */
    @GetMapping("/thread/{threadId}")
    public ResponseEntity<List<PostDTO>> getPostsByThread(@PathVariable String threadId) {
        List<PostDTO> posts = postService.getPostsByThread(threadId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    /**
     * Obter post por ID
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable String postId) {
        return postService.getPostById(postId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post não encontrado com ID: " + postId));
    }

    /**
     * UC13 — Criar tópico / UC14 — Publicar resposta (todos os autenticados)
     */
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO dto) {
        Post post = convertToEntity(dto);
        return new ResponseEntity<>(convertToDTO(postService.createPost(post)), HttpStatus.CREATED);
    }

    /**
     * Editar post (próprio autor)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable String postId,
            @Valid @RequestBody PostDTO dto) {
        postService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post não encontrado com ID: " + postId));
        Post updated = convertToEntity(dto);
        updated.setPostId(postId);
        return ResponseEntity.ok(convertToDTO(postService.updatePost(updated)));
    }

    /**
     * UC15 — Moderar fórum: eliminar post (Admin, Superadmin)
     */
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        if (!postService.getPostById(postId).isPresent()) {
            throw new ResourceNotFoundException("Post não encontrado com ID: " + postId);
        }
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // ========== Conversão ==========

    private PostDTO convertToDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .postedAt(post.getPostedAt() != null ? post.getPostedAt().toString() : null)
                .threadId(post.getForumThread() != null ? post.getForumThread().getThreadId() : null)
                .userId(post.getAuthor() != null ? post.getAuthor().getUserId() : null)
                .build();
    }

    private Post convertToEntity(PostDTO dto) {
        return Post.builder()
                .postId(dto.getPostId())
                .content(dto.getContent())
                .build();
    }
}
