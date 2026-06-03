package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ContentStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentStatsRepository extends JpaRepository<ContentStats, String> {

    @Query("SELECT cs FROM ContentStats cs WHERE cs.contentItem.contentId = :contentId")
    Optional<ContentStats> findByContentItemId(@Param("contentId") String contentId);

    @Modifying
    @Query("UPDATE ContentStats cs SET cs.viewCount = cs.viewCount + 1, cs.lastUpdated = CURRENT_TIMESTAMP WHERE cs.contentItem.contentId = :contentId")
    void incrementViews(@Param("contentId") String contentId);

    @Modifying
    @Query("UPDATE ContentStats cs SET cs.shareCount = cs.shareCount + 1, cs.lastUpdated = CURRENT_TIMESTAMP WHERE cs.contentItem.contentId = :contentId")
    void incrementShares(@Param("contentId") String contentId);

    @Modifying
    @Query("UPDATE ContentStats cs SET cs.commentCount = cs.commentCount + 1, cs.lastUpdated = CURRENT_TIMESTAMP WHERE cs.contentItem.contentId = :contentId")
    void incrementComments(@Param("contentId") String contentId);

    @Modifying
    @Query("DELETE FROM ContentStats cs WHERE cs.contentItem.contentId = :contentId")
    void deleteByContentItemId(@Param("contentId") String contentId);
}
