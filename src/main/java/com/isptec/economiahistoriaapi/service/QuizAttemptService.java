package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.QuizAttempt;
import com.isptec.economiahistoriaapi.repository.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {
    
    private final QuizAttemptRepository quizAttemptRepository;
    
    public QuizAttempt createAttempt(QuizAttempt attempt) {
        return quizAttemptRepository.save(attempt);
    }
    
    public Optional<QuizAttempt> getAttemptById(String attemptId) {
        return quizAttemptRepository.findById(attemptId);
    }
    
    public List<QuizAttempt> getAttemptsByUser(String userId) {
        return quizAttemptRepository.findByUserId(userId);
    }
    
    public List<QuizAttempt> getAttemptsByQuiz(String quizId) {
        return quizAttemptRepository.findByQuizId(quizId);
    }
    
    public List<QuizAttempt> getAllAttempts() {
        return quizAttemptRepository.findAll();
    }
    
    public QuizAttempt updateAttempt(QuizAttempt attempt) {
        return quizAttemptRepository.save(attempt);
    }
    
    public void deleteAttempt(String attemptId) {
        quizAttemptRepository.deleteById(attemptId);
    }
}
