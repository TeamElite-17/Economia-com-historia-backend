package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String commentId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "commented_at", nullable = false)
    private Date commentedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_item_id")
    private ContentItem contentItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;
    
    @PrePersist
    protected void onCreate() {
        if (this.commentedAt == null) {
            this.commentedAt = new Date();
        }
    }
}
