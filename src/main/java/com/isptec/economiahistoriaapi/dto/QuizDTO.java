package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO {
    
    private String quizId;
    
    @NotBlank(message = "O título do quiz é obrigatório")
    private String title;
    
    private String description;
    
    @NotNull(message = "A pontuação mínima para passar é obrigatória")
    private Integer passingScore;
    
    private String quizModuleId;
}
