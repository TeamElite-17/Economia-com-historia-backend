package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modules")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "module_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String moduleId;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private int moduleOrder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private EducationalApp app;
    
    public abstract void open();
}
