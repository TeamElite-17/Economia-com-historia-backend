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
public class ContentItemDTO {
    
    private String contentId;
    
    @NotBlank(message = "O título é obrigatório")
    private String title;
    
    private String description;
    
    @NotNull(message = "O tipo de mídia é obrigatório")
    private String mediaType;
    
    private String sourceUrl;
    
    private String regionTag;
    
    private String publishedAt;
    
    private String regionId;
    
    private String contentModuleId;

    private Integer durationSeconds;
    private Integer wordCount;
    private String fileUrl;
    private String thumbnailUrl;
    private java.util.List<CategoryDTO> categories;
    private String topicId;
}
