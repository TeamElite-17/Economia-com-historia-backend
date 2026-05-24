package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String quizId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private int passingScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_module_id")
    private QuizModule quizModule;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScoreResult> scoreResults;
    
    public ScoreResult evaluate(QuizAttempt attempt) {
        // Logic for evaluating a quiz attempt
        return null;
    }
}
