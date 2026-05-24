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
public class PostDTO {
    
    private String postId;
    
    @NotBlank(message = "O conteúdo do post é obrigatório")
    private String content;
    
    private String postedAt;
    
    private String threadId;
    
    private String userId;
}
