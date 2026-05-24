package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registo de gosto (like) único por utilizador por ContentItem.
 * Garante que cada utilizador só pode dar um gosto por conteúdo.
 */
@Entity
@Table(name = "content_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"content_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem contentItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at", nullable = false)
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.likedAt == null) this.likedAt = LocalDateTime.now();
    }
}
