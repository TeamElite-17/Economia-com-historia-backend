package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.enums.ContentStatus;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.repository.ContentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentItemService {

    private final ContentItemRepository contentItemRepository;

    public ContentItem createContentItem(ContentItem contentItem) {
        return contentItemRepository.save(contentItem);
    }

    public Optional<ContentItem> getContentItemById(String contentId) {
        return contentItemRepository.findById(contentId);
    }

    public List<ContentItem> getContentByModule(String contentModuleId) {
        return contentItemRepository.findByContentModuleId(contentModuleId);
    }

    public List<ContentItem> getContentByRegion(String regionTag) {
        return contentItemRepository.findByRegionTag(regionTag);
    }

    public List<ContentItem> getContentByStatus(ContentStatus status) {
        return contentItemRepository.findByStatus(status);
    }

    public List<ContentItem> getContentByAuthor(String authorId) {
        return contentItemRepository.findByAuthorId(authorId);
    }

    public List<ContentItem> getAllContentItems() {
        return contentItemRepository.findAll();
    }

    public ContentItem updateContentItem(ContentItem contentItem) {
        return contentItemRepository.save(contentItem);
    }

    public void deleteContentItem(String contentId) {
        contentItemRepository.deleteById(contentId);
    }
}

