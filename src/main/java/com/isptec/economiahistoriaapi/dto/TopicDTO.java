package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicDTO {
    private String topicId;

    @NotBlank(message = "O nome do tópico é obrigatório")
    @Size(max = 150)
    private String name;

    @Size(max = 180)
    private String slug;

    private String description;
    private LocalDateTime createdAt;
    private String createdByUserId;
    private String createdByName;
}
