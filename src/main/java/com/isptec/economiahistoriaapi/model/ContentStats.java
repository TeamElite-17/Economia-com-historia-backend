package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Estatísticas de interação por ContentItem.
 * Regista visualizações, partilhas e comentários.
 * Os gostos são contados via ContentLike.
 */
@Entity
@Table(name = "content_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String statsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private ContentItem contentItem;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long shareCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long commentCount = 0L;

    @Column(name = "last_updated")
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
