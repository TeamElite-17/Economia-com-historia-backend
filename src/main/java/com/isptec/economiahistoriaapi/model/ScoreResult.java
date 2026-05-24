package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "score_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resultId;
    
    @Column(nullable = false)
    private int totalScore;
    
    @Column(nullable = false)
    private boolean passed;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
