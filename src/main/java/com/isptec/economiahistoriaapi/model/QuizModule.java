package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "quiz_modules")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class QuizModule extends Module {
    
    @Column(nullable = false)
    private int scoreWeight;
    
    @OneToMany(mappedBy = "quizModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;
    
    @Override
    public void open() {
        // Logic for opening quiz module
    }
    
    public QuizAttempt startQuiz(Quiz quiz) {
        // Logic for starting a quiz
        return null;
    }
}
