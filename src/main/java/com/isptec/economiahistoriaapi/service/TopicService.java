package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.dto.TopicDTO;
import com.isptec.economiahistoriaapi.exception.ConflictException;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Topic;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.TopicRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public List<TopicDTO> findAll() {
        return topicRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TopicDTO findById(String id) {
        return topicRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado: " + id));
    }

    @Transactional
    public TopicDTO create(TopicDTO dto, String creatorEmail) {
        if (topicRepository.existsByName(dto.getName())) {
            throw new ConflictException("Já existe um tópico com o nome: " + dto.getName());
        }
        String slug = dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getName());
        if (topicRepository.existsBySlug(slug)) {
            throw new ConflictException("Já existe um tópico com o slug: " + slug);
        }

        User creator = creatorEmail != null
                ? userRepository.findByEmail(creatorEmail).orElse(null)
                : null;

        Topic topic = Topic.builder()
                .name(dto.getName())
                .slug(slug)
                .description(dto.getDescription())
                .createdBy(creator)
                .build();

        return toDTO(topicRepository.save(topic));
    }

    @Transactional
    public TopicDTO update(String id, TopicDTO dto) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado: " + id));

        topic.setName(dto.getName());
        if (dto.getSlug() != null) topic.setSlug(dto.getSlug());
        if (dto.getDescription() != null) topic.setDescription(dto.getDescription());

        return toDTO(topicRepository.save(topic));
    }

    @Transactional
    public void delete(String id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tópico não encontrado: " + id);
        }
        topicRepository.deleteById(id);
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

    private TopicDTO toDTO(Topic topic) {
        return TopicDTO.builder()
                .topicId(topic.getTopicId())
                .name(topic.getName())
                .slug(topic.getSlug())
                .description(topic.getDescription())
                .createdAt(topic.getCreatedAt())
                .createdByUserId(topic.getCreatedBy() != null ? topic.getCreatedBy().getUserId() : null)
                .createdByName(topic.getCreatedBy() != null ? topic.getCreatedBy().getName() : null)
                .build();
    }
}
