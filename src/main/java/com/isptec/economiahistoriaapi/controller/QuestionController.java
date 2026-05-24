package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.QuestionDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Question;
import com.isptec.economiahistoriaapi.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@Tag(name = "06. Perguntas de Quiz", description = "UC09 Criar e gerir perguntas de avaliação")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * UC10/UC11 — Listar perguntas de um quiz (todos os autenticados)
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuiz(@PathVariable String quizId) {
        List<QuestionDTO> questions = questionService.getQuestionsByQuiz(quizId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    /**
     * Obter pergunta por ID
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable String questionId) {
        return questionService.getQuestionById(questionId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pergunta não encontrada com ID: " + questionId));
    }

    /**
     * UC07/UC09 — Criar pergunta de quiz (Escritor)
     */
    @PostMapping
    @PreAuthorize("hasRole('ESCRITOR')")
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO dto) {
        Question question = convertToEntity(dto);
        return new ResponseEntity<>(convertToDTO(questionService.createQuestion(question)), HttpStatus.CREATED);
    }

    /**
     * UC08 — Editar pergunta (Escritor, Revisor)
     */
    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('ESCRITOR', 'REVISOR')")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable String questionId,
            @Valid @RequestBody QuestionDTO dto) {
        questionService.getQuestionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pergunta não encontrada com ID: " + questionId));
        Question updated = convertToEntity(dto);
        updated.setQuestionId(questionId);
        return ResponseEntity.ok(convertToDTO(questionService.updateQuestion(updated)));
    }

    /**
     * Eliminar pergunta (Admin, Superadmin)
     */
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String questionId) {
        if (!questionService.getQuestionById(questionId).isPresent()) {
            throw new ResourceNotFoundException("Pergunta não encontrada com ID: " + questionId);
        }
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // ========== Conversão ==========

    private QuestionDTO convertToDTO(Question q) {
        return QuestionDTO.builder()
                .questionId(q.getQuestionId())
                .text(q.getText())
                .type(q.getType() != null ? q.getType().toString() : null)
                .points(q.getPoints())
                .quizId(q.getQuiz() != null ? q.getQuiz().getQuizId() : null)
                .build();
    }

    private Question convertToEntity(QuestionDTO dto) {
        return Question.builder()
                .questionId(dto.getQuestionId())
                .text(dto.getText())
                .points(dto.getPoints())
                .build();
    }
}
