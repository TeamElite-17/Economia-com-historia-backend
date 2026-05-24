package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.RegionIndicator;
import com.isptec.economiahistoriaapi.service.RegionIndicatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
@Tag(name = "05. Regiões e Indicadores", description = "UC10 Explorar mapa interativo e indicadores históricos/económicos")
public class RegionIndicatorController {

    private final RegionIndicatorService regionIndicatorService;

    /**
     * UC10 — Explorar regiões geográficas e indicadores (todos os atores autenticados)
     */
    @GetMapping
    public ResponseEntity<List<RegionIndicator>> getAllRegions() {
        return ResponseEntity.ok(regionIndicatorService.getAllRegions());
    }

    /**
     * UC10 — Ver detalhes de uma região específica
     */
    @GetMapping("/{regionId}")
    public ResponseEntity<RegionIndicator> getRegionById(@PathVariable String regionId) {
        return regionIndicatorService.getRegionById(regionId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Região não encontrada com ID: " + regionId));
    }

    /**
     * Criar nova região (Admin, Superadmin)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<RegionIndicator> createRegion(@RequestBody RegionIndicator region) {
        return new ResponseEntity<>(regionIndicatorService.createRegion(region), HttpStatus.CREATED);
    }

    /**
     * Atualizar região (Admin, Superadmin)
     */
    @PutMapping("/{regionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<RegionIndicator> updateRegion(
            @PathVariable String regionId,
            @RequestBody RegionIndicator region) {

        regionIndicatorService.getRegionById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Região não encontrada com ID: " + regionId));
        region.setRegionId(regionId);
        return ResponseEntity.ok(regionIndicatorService.updateRegion(region));
    }

    /**
     * Eliminar região (Superadmin)
     */
    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteRegion(@PathVariable String regionId) {
        if (!regionIndicatorService.getRegionById(regionId).isPresent()) {
            throw new ResourceNotFoundException("Região não encontrada com ID: " + regionId);
        }
        regionIndicatorService.deleteRegion(regionId);
        return ResponseEntity.noContent().build();
    }
}
