package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.ContentItemDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.service.ContentItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/content-items")
@RequiredArgsConstructor
public class ContentItemController {
    
    private final ContentItemService contentItemService;
    
    /**
     * Obter detalhes de um item de conteúdo específico
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentItemDTO> getContentDetail(@PathVariable String contentId) {
        return contentItemService.getContentItemById(contentId)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item de conteúdo não encontrado com ID: " + contentId));
    }
    
    /**
     * Listar todos os itens de conteúdo
     */
    @GetMapping
    public ResponseEntity<List<ContentItemDTO>> getAllContentItems() {
        List<ContentItemDTO> items = contentItemService.getAllContentItems()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    /**
     * Listar itens de conteúdo por módulo
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ContentItemDTO>> getContentByModule(@PathVariable String moduleId) {
        List<ContentItemDTO> items = contentItemService.getContentByModule(moduleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    /**
     * Listar itens de conteúdo por região
     */
    @GetMapping("/region/{regionTag}")
    public ResponseEntity<List<ContentItemDTO>> getContentByRegion(@PathVariable String regionTag) {
        List<ContentItemDTO> items = contentItemService.getContentByRegion(regionTag)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
    /**
     * Criar um novo item de conteúdo
     */
    @PostMapping
    public ResponseEntity<ContentItemDTO> createContentItem(@Valid @RequestBody ContentItemDTO contentItemDTO) {
        ContentItem contentItem = convertToEntity(contentItemDTO);
        ContentItem savedItem = contentItemService.createContentItem(contentItem);
        return new ResponseEntity<>(convertToDTO(savedItem), HttpStatus.CREATED);
    }
    
    /**
     * Atualizar um item de conteúdo
     */
    @PutMapping("/{contentId}")
    public ResponseEntity<ContentItemDTO> updateContentItem(
            @PathVariable String contentId,
            @Valid @RequestBody ContentItemDTO contentItemDTO) {
        
        ContentItem existingItem = contentItemService.getContentItemById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item de conteúdo não encontrado com ID: " + contentId));
        
        ContentItem updatedItem = convertToEntity(contentItemDTO);
        updatedItem.setContentId(contentId);
        ContentItem savedItem = contentItemService.updateContentItem(updatedItem);
        
        return new ResponseEntity<>(convertToDTO(savedItem), HttpStatus.OK);
    }
    
    /**
     * Deletar um item de conteúdo
     */
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContentItem(@PathVariable String contentId) {
        if (!contentItemService.getContentItemById(contentId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Item de conteúdo não encontrado com ID: " + contentId);
        }
        contentItemService.deleteContentItem(contentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // ========== Métodos de Conversão ==========
    
    private ContentItemDTO convertToDTO(ContentItem contentItem) {
        return ContentItemDTO.builder()
                .contentId(contentItem.getContentId())
                .title(contentItem.getTitle())
                .description(contentItem.getDescription())
                .mediaType(contentItem.getMediaType().toString())
                .sourceUrl(contentItem.getSourceUrl())
                .regionTag(contentItem.getRegionTag())
                .publishedAt(contentItem.getPublishedAt() != null ? 
                        contentItem.getPublishedAt().toString() : null)
                .regionId(contentItem.getRegionIndicator() != null ? 
                        contentItem.getRegionIndicator().getRegionId() : null)
                .contentModuleId(contentItem.getContentModule() != null ? 
                        contentItem.getContentModule().getModuleId() : null)
                .build();
    }
    
    private ContentItem convertToEntity(ContentItemDTO contentItemDTO) {
        return ContentItem.builder()
                .contentId(contentItemDTO.getContentId())
                .title(contentItemDTO.getTitle())
                .description(contentItemDTO.getDescription())
                .sourceUrl(contentItemDTO.getSourceUrl())
                .regionTag(contentItemDTO.getRegionTag())
                .build();
    }
}
