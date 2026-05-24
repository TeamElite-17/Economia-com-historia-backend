package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.ContentItemDTO;
import com.isptec.economiahistoriaapi.dto.CategoryDTO;
import com.isptec.economiahistoriaapi.enums.ContentStatus;
import com.isptec.economiahistoriaapi.enums.MediaType;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.model.Category;
import com.isptec.economiahistoriaapi.repository.CategoryRepository;
import com.isptec.economiahistoriaapi.repository.TopicRepository;
import com.isptec.economiahistoriaapi.service.ContentItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/content-items")
@RequiredArgsConstructor
@Tag(name = "04. Conteúdos Didáticos", description = "UC07 Criar rascunho · UC08 Rever · UC09 Aprovar/Publicar · UC10 Visualizar")
public class ContentItemController {

    private final ContentItemService contentItemService;
    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;

    /** UC10 — Visualizar conteúdos publicados (todos os atores autenticados) */
    @GetMapping
    public ResponseEntity<List<ContentItemDTO>> getPublishedContent() {
        List<ContentItemDTO> items = contentItemService.getContentByStatus(ContentStatus.PUBLISHED)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /** UC10 — Detalhe de um item de conteúdo */
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentItemDTO> getContentDetail(@PathVariable String contentId) {
        return contentItemService.getContentItemById(contentId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conteúdo não encontrado com ID: " + contentId));
    }

    /** UC10 — Listar por módulo */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ContentItemDTO>> getContentByModule(@PathVariable String moduleId) {
        return ResponseEntity.ok(contentItemService.getContentByModule(moduleId)
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    /** UC10 — Listar por região */
    @GetMapping("/region/{regionTag}")
    public ResponseEntity<List<ContentItemDTO>> getContentByRegion(@PathVariable String regionTag) {
        return ResponseEntity.ok(contentItemService.getContentByRegion(regionTag)
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    /** UC10 — Listar por tópico */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ContentItemDTO>> getContentByTopic(@PathVariable String topicId) {
        return ResponseEntity.ok(contentItemService.getContentByTopic(topicId)
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    /** Listar rascunhos e conteúdos em revisão (Revisor e Aprovador) */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('REVISOR', 'APROVADOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ContentItemDTO>> getPendingContent() {
        List<ContentItemDTO> items = contentItemService.getContentByStatus(ContentStatus.UNDER_REVIEW)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /** UC07 — Criar rascunho de conteúdo (Escritor) */
    @PostMapping
    @PreAuthorize("hasRole('ESCRITOR')")
    public ResponseEntity<ContentItemDTO> createDraft(@Valid @RequestBody ContentItemDTO dto) {
        ContentItem item = convertToEntity(dto);
        item.setStatus(ContentStatus.DRAFT);
        return new ResponseEntity<>(convertToDTO(contentItemService.createContentItem(item)), HttpStatus.CREATED);
    }

    /** UC08 — Editar conteúdo (Escritor edita os seus; Revisor edita qualquer rascunho) */
    @PutMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('ESCRITOR', 'REVISOR', 'ADMIN')")
    public ResponseEntity<ContentItemDTO> updateContent(
            @PathVariable String contentId,
            @Valid @RequestBody ContentItemDTO dto) {
        ContentItem existing = contentItemService.getContentItemById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conteúdo não encontrado com ID: " + contentId));
        ContentItem updated = convertToEntity(dto);
        updated.setContentId(contentId);
        updated.setStatus(existing.getStatus());
        return ResponseEntity.ok(convertToDTO(contentItemService.updateContentItem(updated)));
    }

    /** UC08 — Submeter para revisão (Escritor) */
    @PatchMapping("/{contentId}/submit")
    @PreAuthorize("hasRole('ESCRITOR')")
    public ResponseEntity<ContentItemDTO> submitForReview(@PathVariable String contentId) {
        ContentItem item = contentItemService.getContentItemById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conteúdo não encontrado com ID: " + contentId));
        item.setStatus(ContentStatus.UNDER_REVIEW);
        return ResponseEntity.ok(convertToDTO(contentItemService.updateContentItem(item)));
    }

    /** UC09 — Aprovar e publicar conteúdo (Aprovador) */
    @PatchMapping("/{contentId}/approve")
    @PreAuthorize("hasRole('APROVADOR')")
    public ResponseEntity<ContentItemDTO> approveContent(@PathVariable String contentId) {
        ContentItem item = contentItemService.getContentItemById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conteúdo não encontrado com ID: " + contentId));
        item.setStatus(ContentStatus.PUBLISHED);
        item.setPublishedAt(new Date());
        return ResponseEntity.ok(convertToDTO(contentItemService.updateContentItem(item)));
    }

    /** UC09 — Rejeitar conteúdo (Aprovador) */
    @PatchMapping("/{contentId}/reject")
    @PreAuthorize("hasRole('APROVADOR')")
    public ResponseEntity<ContentItemDTO> rejectContent(@PathVariable String contentId) {
        ContentItem item = contentItemService.getContentItemById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conteúdo não encontrado com ID: " + contentId));
        item.setStatus(ContentStatus.REJECTED);
        return ResponseEntity.ok(convertToDTO(contentItemService.updateContentItem(item)));
    }

    /** Eliminar conteúdo (Admin) */
    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteContentItem(@PathVariable String contentId) {
        if (!contentItemService.getContentItemById(contentId).isPresent()) {
            throw new ResourceNotFoundException("Conteúdo não encontrado com ID: " + contentId);
        }
        contentItemService.deleteContentItem(contentId);
        return ResponseEntity.noContent().build();
    }

    // ========== Conversão ==========

    private ContentItemDTO convertToDTO(ContentItem item) {
        return ContentItemDTO.builder()
                .contentId(item.getContentId())
                .title(item.getTitle())
                .description(item.getDescription())
                .mediaType(item.getMediaType() != null ? item.getMediaType().toString() : null)
                .sourceUrl(item.getSourceUrl())
                .regionTag(item.getRegionTag())
                .publishedAt(item.getPublishedAt() != null ? item.getPublishedAt().toString() : null)
                .regionId(item.getRegionIndicator() != null ? item.getRegionIndicator().getRegionId() : null)
                .contentModuleId(item.getContentModule() != null ? item.getContentModule().getModuleId() : null)
                .durationSeconds(item.getDurationSeconds())
                .wordCount(item.getWordCount())
                .fileUrl(item.getFileUrl())
                .thumbnailUrl(item.getThumbnailUrl())
                .topicId(item.getTopic() != null ? item.getTopic().getTopicId() : null)
                .categories(item.getCategories() != null ? item.getCategories().stream()
                        .map(cat -> CategoryDTO.builder()
                                .categoryId(cat.getCategoryId())
                                .name(cat.getName())
                                .slug(cat.getSlug())
                                .description(cat.getDescription())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private ContentItem convertToEntity(ContentItemDTO dto) {
        ContentItem item = ContentItem.builder()
                .contentId(dto.getContentId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .mediaType(dto.getMediaType() != null ? MediaType.valueOf(dto.getMediaType().toUpperCase()) : null)
                .sourceUrl(dto.getSourceUrl())
                .regionTag(dto.getRegionTag())
                .durationSeconds(dto.getDurationSeconds())
                .wordCount(dto.getWordCount())
                .fileUrl(dto.getFileUrl())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();

        if (dto.getTopicId() != null) {
            topicRepository.findById(dto.getTopicId()).ifPresent(item::setTopic);
        }

        if (dto.getCategories() != null) {
            List<Category> cats = dto.getCategories().stream()
                    .map(catDto -> {
                        if (catDto.getCategoryId() != null) {
                            return categoryRepository.findById(catDto.getCategoryId()).orElse(null);
                        } else if (catDto.getName() != null) {
                            return categoryRepository.findByName(catDto.getName()).orElse(null);
                        }
                        return null;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            item.setCategories(cats);
        }
        return item;
    }
}
