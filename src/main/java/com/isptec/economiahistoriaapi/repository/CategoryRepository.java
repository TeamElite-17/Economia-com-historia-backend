package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
