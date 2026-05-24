package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptDTO {
    
    private String attemptId;
    
    @NotNull(message = "O ID do quiz é obrigatório")
    private String quizId;
    
    @NotNull(message = "O ID do utilizador é obrigatório")
    private String userId;
    
    @NotNull(message = "A pontuação é obrigatória")
    private Integer score;
    
    private Boolean completed;
    
    private String takenAt;
}
