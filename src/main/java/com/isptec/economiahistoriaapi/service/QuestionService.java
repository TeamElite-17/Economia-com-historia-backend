package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.Question;
import com.isptec.economiahistoriaapi.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    public Optional<Question> getQuestionById(String questionId) {
        return questionRepository.findById(questionId);
    }
    
    public List<Question> getQuestionsByQuiz(String quizId) {
        return questionRepository.findByQuizId(quizId);
    }
    
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    public void deleteQuestion(String questionId) {
        questionRepository.deleteById(questionId);
    }
}
