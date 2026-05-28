package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "post_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "like_id")
    private String likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        if (this.likedAt == null) {
            this.likedAt = LocalDateTime.now();
        }
    }
}
