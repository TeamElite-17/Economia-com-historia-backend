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
public class ForumThreadDTO {
    
    private String threadId;
    
    @NotBlank(message = "O título da thread é obrigatório")
    private String title;
    
    private String createdAt;
    
    private String forumModuleId;
}
