package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem, String> {
    List<ContentItem> findByContentModuleId(String contentModuleId);
    List<ContentItem> findByRegionTag(String regionTag);
}
