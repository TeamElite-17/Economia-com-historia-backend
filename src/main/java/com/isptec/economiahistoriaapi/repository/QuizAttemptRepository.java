package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {
    List<QuizAttempt> findByUserId(String userId);
    List<QuizAttempt> findByQuizId(String quizId);
}
