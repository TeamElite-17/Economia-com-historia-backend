package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.RegionIndicator;
import com.isptec.economiahistoriaapi.repository.RegionIndicatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionIndicatorService {
    
    private final RegionIndicatorRepository regionIndicatorRepository;
    
    public RegionIndicator createRegion(RegionIndicator region) {
        return regionIndicatorRepository.save(region);
    }
    
    public Optional<RegionIndicator> getRegionById(String regionId) {
        return regionIndicatorRepository.findById(regionId);
    }
    
    public List<RegionIndicator> getAllRegions() {
        return regionIndicatorRepository.findAll();
    }
    
    public RegionIndicator updateRegion(RegionIndicator region) {
        return regionIndicatorRepository.save(region);
    }
    
    public void deleteRegion(String regionId) {
        regionIndicatorRepository.deleteById(regionId);
    }
}
