package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "content_modules")
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class ContentModule extends Module {
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "content_module_topics",
        joinColumns = @JoinColumn(name = "module_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<Topic> topics;
    
    @OneToMany(mappedBy = "contentModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContentItem> contentItems;
    
    @Override
    public void open() {
        displayContent();
    }
    
    public void displayContent() {
        // Logic for displaying content
    }
}
