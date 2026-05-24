package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    
    private String commentId;
    
    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    private String content;
    
    private String commentedAt;
    
    private String postId;
    
    private String userId;
}
