package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection, String> {

    List<UserCollection> findByUserIdAndItemType(String userId, String itemType);

    Optional<UserCollection> findByUserIdAndItemTypeAndItemId(String userId, String itemType, String itemId);

    List<UserCollection> findByItemTypeAndItemId(String itemType, String itemId);

    @Transactional
    void deleteByUserIdAndItemTypeAndItemId(String userId, String itemType, String itemId);
}
