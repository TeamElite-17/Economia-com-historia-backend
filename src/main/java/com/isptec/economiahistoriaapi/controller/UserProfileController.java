package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.UserDTO;
import com.isptec.economiahistoriaapi.dto.UserProfileDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.UserProfile;
import com.isptec.economiahistoriaapi.service.UserProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "02. Perfis", description = "UC03 Gerir perfil de utilizador (bio, escolaridade, região)")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * UC03 — Ver perfil de um utilizador
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDTO> getProfileByUserId(@PathVariable String userId) {
        return userProfileService.getProfileByUserId(userId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil não encontrado para o utilizador: " + userId));
    }

    /**
     * UC03 — Ver perfil por profileId
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<UserProfileDTO> getProfileById(@PathVariable String profileId) {
        return userProfileService.getProfileById(profileId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil não encontrado com ID: " + profileId));
    }

    /**
     * UC03 — Criar perfil
     */
    @PostMapping
    public ResponseEntity<UserProfileDTO> createProfile(@Valid @RequestBody UserProfileDTO dto) {
        UserProfile profile = convertToEntity(dto);
        UserProfile saved = userProfileService.createProfile(profile);
        return new ResponseEntity<>(convertToDTO(saved), HttpStatus.CREATED);
    }

    /**
     * UC03 — Atualizar perfil (dados pessoais, biografia, escolaridade, região)
     */
    @PutMapping("/{profileId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable String profileId,
            @Valid @RequestBody UserProfileDTO dto) {

        UserProfile existing = userProfileService.getProfileById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil não encontrado com ID: " + profileId));

        UserProfile updated = convertToEntity(dto);
        updated.setProfileId(profileId);
        updated.setUser(existing.getUser());

        return ResponseEntity.ok(convertToDTO(userProfileService.updateProfile(updated)));
    }

    // ========== Conversão ==========

    private UserProfileDTO convertToDTO(UserProfile profile) {
        return UserProfileDTO.builder()
                .profileId(profile.getProfileId())
                .bio(profile.getBio())
                .ageRange(profile.getAgeRange())
                .educationLevel(profile.getEducationLevel())
                .region(profile.getRegion())
                .userId(profile.getUser() != null ? profile.getUser().getUserId() : null)
                .build();
    }

    private UserProfile convertToEntity(UserProfileDTO dto) {
        return UserProfile.builder()
                .bio(dto.getBio())
                .ageRange(dto.getAgeRange())
                .educationLevel(dto.getEducationLevel())
                .region(dto.getRegion())
                .build();
    }
}
