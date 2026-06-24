package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.enums.ContentStatus;
import com.isptec.economiahistoriaapi.model.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem, String> {

    List<ContentItem> findByRegionTag(String regionTag);
    List<ContentItem> findByStatus(ContentStatus status);
    List<ContentItem> findByAuthorId(String authorId);
    @Query("SELECT c FROM ContentItem c WHERE c.topic.topicId = :topicId")
    List<ContentItem> findByTopicTopicId(@Param("topicId") String topicId);
}
