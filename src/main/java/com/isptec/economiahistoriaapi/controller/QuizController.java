package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.dto.QuizDTO;
import com.isptec.economiahistoriaapi.exception.ResourceNotFoundException;
import com.isptec.economiahistoriaapi.model.Quiz;
import com.isptec.economiahistoriaapi.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {
    
    private final QuizService quizService;
    
    /**
     * Obter detalhes de um quiz
     */
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable String quizId) {
        return quizService.getQuizById(quizId)
                .map(this::convertToDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz não encontrado com ID: " + quizId));
    }
    
    /**
     * Listar todos os quizzes
     */
    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<QuizDTO> quizzes = quizService.getAllQuizzes()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }
    
    /**
     * Listar quizzes por módulo
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByModule(@PathVariable String moduleId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByModule(moduleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }
    
    /**
     * Criar novo quiz
     */
    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        Quiz quiz = convertToEntity(quizDTO);
        Quiz savedQuiz = quizService.createQuiz(quiz);
        return new ResponseEntity<>(convertToDTO(savedQuiz), HttpStatus.CREATED);
    }
    
    /**
     * Atualizar quiz
     */
    @PutMapping("/{quizId}")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable String quizId,
            @Valid @RequestBody QuizDTO quizDTO) {
        
        Quiz existingQuiz = quizService.getQuizById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz não encontrado com ID: " + quizId));
        
        Quiz updatedQuiz = convertToEntity(quizDTO);
        updatedQuiz.setQuizId(quizId);
        Quiz savedQuiz = quizService.updateQuiz(updatedQuiz);
        
        return new ResponseEntity<>(convertToDTO(savedQuiz), HttpStatus.OK);
    }
    
    /**
     * Deletar quiz
     */
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable String quizId) {
        if (!quizService.getQuizById(quizId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Quiz não encontrado com ID: " + quizId);
        }
        quizService.deleteQuiz(quizId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // ========== Métodos de Conversão ==========
    
    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .quizId(quiz.getQuizId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passingScore(quiz.getPassingScore())
                .quizModuleId(quiz.getQuizModule() != null ? 
                        quiz.getQuizModule().getModuleId() : null)
                .build();
    }
    
    private Quiz convertToEntity(QuizDTO quizDTO) {
        return Quiz.builder()
                .title(quizDTO.getTitle())
                .description(quizDTO.getDescription())
                .passingScore(quizDTO.getPassingScore())
                .build();
    }
}
