package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    
    private String questionId;
    
    @NotBlank(message = "O texto da questão é obrigatório")
    private String text;
    
    @NotNull(message = "O tipo de questão é obrigatório")
    private String type;
    
    @NotNull(message = "A pontuação é obrigatória")
    private Integer points;
    
    private String quizId;

    private List<AnswerOptionDTO> answerOptions;
}
