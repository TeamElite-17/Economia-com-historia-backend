package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String optionId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Column(nullable = false)
    private boolean correct;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
