package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String participationId;
    
    @Column(length = 100)
    private String type;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public void submitReflection() {
        // Logic for submitting a reflection
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }
}
