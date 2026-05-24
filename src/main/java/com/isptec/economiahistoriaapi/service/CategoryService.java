package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.dto.CategoryDTO;
import com.isptec.economiahistoriaapi.exception.ConflictException;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Category;
import com.isptec.economiahistoriaapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(String id) {
        return categoryRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    public CategoryDTO findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException("Já existe uma categoria com o nome: " + dto.getName());
        }
        String slug = dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new ConflictException("Já existe uma categoria com o slug: " + slug);
        }

        Category category = Category.builder()
                .name(dto.getName())
                .slug(slug)
                .description(dto.getDescription())
                .build();

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoria pai não encontrada: " + dto.getParentCategoryId()));
            category.setParentCategory(parent);
        }

        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO update(String id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));

        category.setName(dto.getName());
        if (dto.getSlug() != null) category.setSlug(dto.getSlug());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoria pai não encontrada: " + dto.getParentCategoryId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada: " + id);
        }
        categoryRepository.deleteById(id);
    }

    // ===== Helpers =====

    private String generateSlug(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    private CategoryDTO toDTO(Category cat) {
        return CategoryDTO.builder()
                .categoryId(cat.getCategoryId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .description(cat.getDescription())
                .parentCategoryId(cat.getParentCategory() != null ? cat.getParentCategory().getCategoryId() : null)
                .parentCategoryName(cat.getParentCategory() != null ? cat.getParentCategory().getName() : null)
                .build();
    }
}
