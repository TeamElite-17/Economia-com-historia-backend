package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "educational_apps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationalApp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String appId;
    
    @Column(nullable = false, length = 100)
    private String appName;
    
    @Column(length = 50)
    private String version;
    
    @Column(length = 10)
    private String language;
    
    @Column(length = 50)
    private String theme;
    
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Module> modules;
    
    public void launch() {
        // Logic for launching the application
    }
    
    public Module loadModule(String moduleId) {
        // Logic for loading a module
        return null;
    }
    
    public void registerUser(User user) {
        // Logic for registering a user
    }
}
