package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "forum_threads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumThread {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String threadId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_module_id")
    private ForumModule forumModule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;
    
    @OneToMany(mappedBy = "forumThread", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;
    
    public void addPost(Post post) {
        if (this.posts == null) {
            this.posts = new java.util.ArrayList<>();
        }
        this.posts.add(post);
        post.setForumThread(this);
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }
}
