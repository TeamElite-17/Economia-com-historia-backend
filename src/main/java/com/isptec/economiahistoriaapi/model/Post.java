package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posted_at", nullable = false)
    private Date postedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_thread_id")
    private ForumThread forumThread;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    
    public void reply(Comment comment) {
        if (this.comments == null) {
            this.comments = new java.util.ArrayList<>();
        }
        this.comments.add(comment);
        comment.setPost(this);
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.postedAt == null) {
            this.postedAt = new Date();
        }
    }
}
