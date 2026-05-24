package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.dto.ContentStatsDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.ContentItem;
import com.isptec.economiahistoriaapi.model.ContentLike;
import com.isptec.economiahistoriaapi.model.ContentStats;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.ContentItemRepository;
import com.isptec.economiahistoriaapi.repository.ContentLikeRepository;
import com.isptec.economiahistoriaapi.repository.ContentStatsRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentStatsService {

    private final ContentStatsRepository statsRepository;
    private final ContentLikeRepository likeRepository;
    private final ContentItemRepository contentItemRepository;
    private final UserRepository userRepository;

    /** Obtém as estatísticas completas de um conteúdo, incluindo se o utilizador atual já deu gosto. */
    @Transactional(readOnly = true)
    public ContentStatsDTO getStats(String contentId, String currentUserEmail) {
        ContentStats stats = getOrCreate(contentId);
        Long likeCount = likeRepository.countByContentId(contentId);

        Boolean likedByMe = null;
        if (currentUserEmail != null) {
            userRepository.findByEmail(currentUserEmail).ifPresent(user -> {});
            likedByMe = userRepository.findByEmail(currentUserEmail)
                    .map(u -> likeRepository.existsByContentAndUser(contentId, u.getUserId()))
                    .orElse(false);
        }

        return ContentStatsDTO.builder()
                .contentId(contentId)
                .viewCount(stats.getViewCount())
                .shareCount(stats.getShareCount())
                .commentCount(stats.getCommentCount())
                .likeCount(likeCount)
                .lastUpdated(stats.getLastUpdated())
                .likedByCurrentUser(likedByMe)
                .build();
    }

    /** Regista uma visualização. */
    @Transactional
    public void registerView(String contentId) {
        ensureExists(contentId);
        getOrCreate(contentId);
        statsRepository.incrementViews(contentId);
    }

    /** Regista uma partilha. */
    @Transactional
    public void registerShare(String contentId) {
        ensureExists(contentId);
        getOrCreate(contentId);
        statsRepository.incrementShares(contentId);
    }

    /**
     * Toggle de gosto: se já deu gosto remove, caso contrário adiciona.
     * Retorna true se agora tem gosto, false se foi removido.
     */
    @Transactional
    public boolean toggleLike(String contentId, String userEmail) {
        ContentItem content = ensureExists(contentId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        return likeRepository.findByContentAndUser(contentId, user.getUserId())
                .map(like -> {
                    likeRepository.delete(like);
                    return false;  // removido
                })
                .orElseGet(() -> {
                    ContentLike like = ContentLike.builder()
                            .contentItem(content)
                            .user(user)
                            .build();
                    likeRepository.save(like);
                    return true;   // adicionado
                });
    }

    // ===== Helpers =====

    private ContentItem ensureExists(String contentId) {
        return contentItemRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + contentId));
    }

    private ContentStats getOrCreate(String contentId) {
        return statsRepository.findByContentItemId(contentId)
                .orElseGet(() -> {
                    ContentItem content = contentItemRepository.findById(contentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + contentId));
                    ContentStats stats = ContentStats.builder().contentItem(content).build();
                    return statsRepository.save(stats);
                });
    }
}
