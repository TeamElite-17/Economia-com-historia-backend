package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.ContentModule;
import com.isptec.economiahistoriaapi.repository.ContentModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentModuleService {
    
    private final ContentModuleRepository contentModuleRepository;
    
    public ContentModule createModule(ContentModule module) {
        return contentModuleRepository.save(module);
    }
    
    public Optional<ContentModule> getModuleById(String moduleId) {
        return contentModuleRepository.findById(moduleId);
    }
    
    public List<ContentModule> getModulesByApp(String appId) {
        return contentModuleRepository.findByAppId(appId);
    }
    
    public List<ContentModule> getAllModules() {
        return contentModuleRepository.findAll();
    }
    
    public ContentModule updateModule(ContentModule module) {
        return contentModuleRepository.save(module);
    }
    
    public void deleteModule(String moduleId) {
        contentModuleRepository.deleteById(moduleId);
    }
}
