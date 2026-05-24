package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByQuizModuleId(String quizModuleId);
}
