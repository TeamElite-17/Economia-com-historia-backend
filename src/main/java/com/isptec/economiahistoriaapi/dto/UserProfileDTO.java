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
public class UserProfileDTO {
    
    private String profileId;
    
    @NotBlank(message = "O userId é obrigatório")
    private String userId;
    
    private String bio;
    
    private String ageRange;
    
    private String educationLevel;
    
    private String region;
    
    private String name;
    
    private String avatarUrl;
    
    private String youtubeUrl;
    
    private String instagramUrl;
    
    private String facebookUrl;
    
    private String websiteUrl;
}
