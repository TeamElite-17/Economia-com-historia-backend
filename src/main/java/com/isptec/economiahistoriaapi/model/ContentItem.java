package com.isptec.economiahistoriaapi.model;

import com.isptec.economiahistoriaapi.enums.ContentStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(name = "author_id", length = 36)
    private String authorId;

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
