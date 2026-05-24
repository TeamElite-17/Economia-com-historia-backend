package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
    Optional<Topic> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
