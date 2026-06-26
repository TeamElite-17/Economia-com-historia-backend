package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String profileId;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(length = 50)
    private String ageRange;
    
    @Column(length = 100)
    private String educationLevel;
    
    @Column(length = 100)
    private String region;
    
    @Column(columnDefinition = "TEXT")
    private String avatarUrl;
    
    @Column(length = 255)
    private String youtubeUrl;
    
    @Column(length = 255)
    private String instagramUrl;
    
    @Column(length = 255)
    private String facebookUrl;
    
    @Column(length = 255)
    private String websiteUrl;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public void editProfile() {
        // Logic for editing profile
    }
}
