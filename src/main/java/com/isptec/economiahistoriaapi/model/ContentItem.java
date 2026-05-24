package com.isptec.economiahistoriaapi.model;

import com.isptec.economiahistoriaapi.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "content_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String contentId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;
    
    @Column(length = 500)
    private String sourceUrl;
    
    @Column(length = 100)
    private String regionTag;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "published_at")
    private Date publishedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionIndicator regionIndicator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_module_id")
    private ContentModule contentModule;
    
    public void play() {
        // Logic for playing media content
    }
    
    public void read() {
        // Logic for reading text content
    }
}
