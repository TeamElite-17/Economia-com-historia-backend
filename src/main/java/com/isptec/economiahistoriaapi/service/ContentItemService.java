package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.enums.ContentStatus;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.repository.ContentItemRepository;
import com.isptec.economiahistoriaapi.repository.ContentStatsRepository;
import com.isptec.economiahistoriaapi.repository.ContentLikeRepository;
import com.isptec.economiahistoriaapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentItemService {

    private final ContentItemRepository contentItemRepository;
    private final ContentStatsRepository contentStatsRepository;
    private final ContentLikeRepository contentLikeRepository;
    private final CommentRepository commentRepository;

    public ContentItem createContentItem(ContentItem contentItem) {
        return contentItemRepository.save(contentItem);
    }

    public Optional<ContentItem> getContentItemById(String contentId) {
        return contentItemRepository.findById(contentId);
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

    public List<ContentItem> getContentByTopic(String topicId) {
        return contentItemRepository.findByTopicTopicId(topicId);
    }

    public ContentItem updateContentItem(ContentItem contentItem) {
        return contentItemRepository.save(contentItem);
    }

    @Transactional
    public void deleteContentItem(String contentId) {
        // Elimina todas as referências ao conteúdo antes de deletar o próprio conteúdo
        // Isso previne erros de foreign key constraint
        contentStatsRepository.deleteByContentItemId(contentId);      // Elimina estatísticas
        contentLikeRepository.deleteByContentItemId(contentId);       // Elimina gostos
        commentRepository.deleteByContentItemId(contentId);           // Elimina comentários
        contentItemRepository.deleteById(contentId);                  // Por fim, elimina o conteúdo
    }
}

