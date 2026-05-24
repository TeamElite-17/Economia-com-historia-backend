package com.isptec.economiahistoriaapi.model;

import com.isptec.economiahistoriaapi.enums.ContentStatus;
import com.isptec.economiahistoriaapi.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // ===== Campos específicos por tipo de media =====

    /** Duração em segundos (VIDEO, PODCAST). */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    /** Contagem de palavras (TEXT/artigos). */
    @Column(name = "word_count")
    private Integer wordCount;

    /** URL do ficheiro de media (vídeo, áudio, documento). */
    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    /** URL da imagem de capa/thumbnail (VIDEO, IMAGE). */
    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    // ===== Relações =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionIndicator regionIndicator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_module_id")
    private ContentModule contentModule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    /** Categorias temáticas do conteúdo. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "content_item_categories",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();
    
    public void play() {
        // Logic for playing media content
    }
    
    public void read() {
        // Logic for reading text content
    }
}
