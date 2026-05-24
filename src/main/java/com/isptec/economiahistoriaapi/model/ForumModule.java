package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "forum_modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumModule extends Module {
    
    @Column(nullable = false)
    private int topicCount;
    
    @OneToMany(mappedBy = "forumModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumThread> threads;
    
    @Override
    public void open() {
        // Logic for opening forum module
    }
    
    public ForumThread createThread(String title) {
        // Logic for creating a forum thread
        return null;
    }
}
