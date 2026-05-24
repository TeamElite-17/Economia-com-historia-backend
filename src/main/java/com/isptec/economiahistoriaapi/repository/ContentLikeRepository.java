package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ContentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, String> {

    @Query("SELECT cl FROM ContentLike cl WHERE cl.contentItem.contentId = :contentId AND cl.user.userId = :userId")
    Optional<ContentLike> findByContentAndUser(@Param("contentId") String contentId, @Param("userId") String userId);

    @Query("SELECT COUNT(cl) FROM ContentLike cl WHERE cl.contentItem.contentId = :contentId")
    Long countByContentId(@Param("contentId") String contentId);

    @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END FROM ContentLike cl WHERE cl.contentItem.contentId = :contentId AND cl.user.userId = :userId")
    boolean existsByContentAndUser(@Param("contentId") String contentId, @Param("userId") String userId);
}
