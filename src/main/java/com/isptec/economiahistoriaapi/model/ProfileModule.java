package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "profile_modules")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ProfileModule extends Module {
    
    @Override
    public void open() {
        // Logic for opening profile module
    }
    
    public void updateProfile() {
        // Logic for updating profile
    }
    
    public void viewHistory() {
        // Logic for viewing history
    }
}
