package com.isptec.economiahistoriaapi.model;

import com.isptec.economiahistoriaapi.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String questionId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Column(nullable = false)
    private int points;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnswerOption> answerOptions;
    
    public boolean validateAnswer(AnswerOption answer) {
        // Logic for validating an answer
        return answer != null && answer.isCorrect();
    }
}
