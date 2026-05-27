package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.AnswerOptionDTO;
import com.isptec.economiahistoriaapi.dto.QuestionDTO;
import com.isptec.economiahistoriaapi.enums.QuestionType;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.AnswerOption;
import com.isptec.economiahistoriaapi.model.Question;
import com.isptec.economiahistoriaapi.model.Quiz;
import com.isptec.economiahistoriaapi.repository.AnswerOptionRepository;
import com.isptec.economiahistoriaapi.repository.QuizRepository;
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
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizRepository quizRepository;

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
    @PreAuthorize("hasAnyRole('ESCRITOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO dto) {
        if (dto.getQuizId() == null || dto.getQuizId().isBlank()) {
            throw new ResourceNotFoundException("O ID do quiz é obrigatório");
        }
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz não encontrado: " + dto.getQuizId()));

        QuestionType type = QuestionType.SINGLE_CHOICE;
        if (dto.getType() != null) {
            type = QuestionType.valueOf(dto.getType().toUpperCase());
        }

        Question question = Question.builder()
                .text(dto.getText())
                .type(type)
                .points(dto.getPoints() != null ? dto.getPoints() : 10)
                .quiz(quiz)
                .build();

        Question saved = questionService.createQuestion(question);

        if (dto.getAnswerOptions() != null) {
            for (AnswerOptionDTO optDto : dto.getAnswerOptions()) {
                answerOptionRepository.save(AnswerOption.builder()
                        .text(optDto.getText())
                        .correct(Boolean.TRUE.equals(optDto.getCorrect()))
                        .question(saved)
                        .build());
            }
        }

        return new ResponseEntity<>(convertToDTO(saved), HttpStatus.CREATED);
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
        List<AnswerOptionDTO> options = answerOptionRepository.findByQuestionId(q.getQuestionId())
                .stream()
                .map(opt -> AnswerOptionDTO.builder()
                        .optionId(opt.getOptionId())
                        .text(opt.getText())
                        .correct(opt.isCorrect())
                        .build())
                .collect(Collectors.toList());

        return QuestionDTO.builder()
                .questionId(q.getQuestionId())
                .text(q.getText())
                .type(q.getType() != null ? q.getType().toString() : null)
                .points(q.getPoints())
                .quizId(q.getQuiz() != null ? q.getQuiz().getQuizId() : null)
                .answerOptions(options)
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
