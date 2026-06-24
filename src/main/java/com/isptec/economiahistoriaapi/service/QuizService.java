package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.Quiz;
import com.isptec.economiahistoriaapi.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {
    
    private final QuizRepository quizRepository;
    
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    public Optional<Quiz> getQuizById(String quizId) {
        return quizRepository.findById(quizId);
    }
    

    
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    
    public Quiz updateQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    public void deleteQuiz(String quizId) {
        quizRepository.deleteById(quizId);
    }
}
