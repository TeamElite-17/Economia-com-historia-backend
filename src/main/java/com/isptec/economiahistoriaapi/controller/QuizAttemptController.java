package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.QuizAttemptDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Quiz;
import com.isptec.economiahistoriaapi.model.QuizAttempt;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.QuizAttemptRepository;
import com.isptec.economiahistoriaapi.repository.QuizRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.QuizAttemptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/quiz-attempts")
@RequiredArgsConstructor
@Tag(name = "07. Tentativas de Quiz", description = "UC11 Realizar quiz · UC12 Consultar resultados e feedback")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    /**
     * Ranking público — agrega tentativas concluídas por utilizador
     */
    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRanking() {
        List<QuizAttempt> all = quizAttemptRepository.findAll();

        // Agrupa por utilizador: soma pontos e conta quizzes distintos completos
        Map<String, Map<String, Object>> byUser = new LinkedHashMap<>();
        for (QuizAttempt qa : all) {
            if (!qa.isCompleted() || qa.getUser() == null) continue;
            String uid = qa.getUser().getUserId();
            byUser.computeIfAbsent(uid, k -> {
                User u = qa.getUser();
                String avatarUrl = "https://ui-avatars.com/api/?name="
                        + URLEncoder.encode(u.getName(), StandardCharsets.UTF_8)
                        + "&background=7B1D2D&color=fff&size=200";
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("userId", uid);
                entry.put("userName", u.getName());
                entry.put("userAvatar", avatarUrl);
                entry.put("points", 0L);
                entry.put("quizzesCompleted", 0);
                return entry;
            });
            Map<String, Object> entry = byUser.get(uid);
            entry.put("points", (Long) entry.get("points") + qa.getScore());
            entry.put("quizzesCompleted", (Integer) entry.get("quizzesCompleted") + 1);
        }

        // Ordena por pontos desc, limita a 50
        List<Map<String, Object>> ranking = byUser.values().stream()
                .sorted((a, b) -> Long.compare((Long) b.get("points"), (Long) a.get("points")))
                .limit(50)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ranking);
    }

    /**
     * UC11 — Realizar Quiz / submeter tentativa (utilizador autenticado)
     */
    @PostMapping
    public ResponseEntity<QuizAttemptDTO> submitAttempt(@RequestBody QuizAttemptDTO dto) {
        QuizAttempt attempt = convertToEntity(dto);
        attempt.submit();
        return new ResponseEntity<>(convertToDTO(quizAttemptService.createAttempt(attempt)), HttpStatus.CREATED);
    }

    /**
     * UC12 — Consultar resultado de uma tentativa específica (Estudante, Revisor, Admin)
     */
    @GetMapping("/{attemptId}")
    @PreAuthorize("hasAnyRole('ESTUDANTE', 'REVISOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<QuizAttemptDTO> getAttemptById(@PathVariable String attemptId) {
        return quizAttemptService.getAttemptById(attemptId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tentativa não encontrada com ID: " + attemptId));
    }

    /**
     * UC12 — Histórico de tentativas de um utilizador (Estudante, Revisor, Admin)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ESTUDANTE', 'REVISOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<QuizAttemptDTO>> getAttemptsByUser(@PathVariable String userId) {
        List<QuizAttemptDTO> attempts = quizAttemptService.getAttemptsByUser(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(attempts);
    }

    /**
     * Listar tentativas por quiz (Revisor, Admin)
     */
    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('REVISOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<QuizAttemptDTO>> getAttemptsByQuiz(@PathVariable String quizId) {
        List<QuizAttemptDTO> attempts = quizAttemptService.getAttemptsByQuiz(quizId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(attempts);
    }

    // ========== Conversão ==========

    private QuizAttemptDTO convertToDTO(QuizAttempt attempt) {
        return QuizAttemptDTO.builder()
                .attemptId(attempt.getAttemptId())
                .score(attempt.getScore())          // int → Integer (auto-box)
                .completed(attempt.isCompleted())   // boolean → Boolean (auto-box)
                .takenAt(attempt.getTakenAt() != null ? attempt.getTakenAt().toString() : null)
                .quizId(attempt.getQuiz() != null ? attempt.getQuiz().getQuizId() : null)
                .userId(attempt.getUser() != null ? attempt.getUser().getUserId() : null)
                .build();
    }

    private QuizAttempt convertToEntity(QuizAttemptDTO dto) {
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz não encontrado: " + dto.getQuizId()));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado: " + dto.getUserId()));

        return QuizAttempt.builder()
                .score(dto.getScore() != null ? dto.getScore() : 0)
                .completed(Boolean.TRUE.equals(dto.getCompleted()))
                .quiz(quiz)
                .user(user)
                .build();
    }
}
