package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region_indicators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionIndicator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String regionId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 500)
    private String mapUrl;
    
    public void highlightRegion() {
        // Logic for highlighting region
    }
}
