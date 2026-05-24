package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.UserProfile;
import com.isptec.economiahistoriaapi.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    public UserProfile createProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }
    
    public Optional<UserProfile> getProfileById(String profileId) {
        return userProfileRepository.findById(profileId);
    }
    
    public Optional<UserProfile> getProfileByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }
    
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }
    
    public UserProfile updateProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }
    
    public void deleteProfile(String profileId) {
        userProfileRepository.deleteById(profileId);
    }
}
