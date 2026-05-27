package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.QuizAttemptDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Quiz;
import com.isptec.economiahistoriaapi.model.QuizAttempt;
import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.QuizRepository;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import com.isptec.economiahistoriaapi.service.QuizAttemptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/quiz-attempts")
@RequiredArgsConstructor
@Tag(name = "07. Tentativas de Quiz", description = "UC11 Realizar quiz · UC12 Consultar resultados e feedback")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

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
