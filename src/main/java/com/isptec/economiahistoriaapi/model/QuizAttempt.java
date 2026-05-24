package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String attemptId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "taken_at")
    private Date takenAt;
    
    @Column(nullable = false)
    private int score;
    
    @Column(nullable = false)
    private boolean completed;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public void submit() {
        this.completed = true;
        this.takenAt = new Date();
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.takenAt == null) {
            this.takenAt = new Date();
        }
    }
}
