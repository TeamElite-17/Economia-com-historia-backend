package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    @Query("SELECT q FROM Quiz q WHERE q.quizModule.moduleId = :moduleId")
    List<Quiz> findByQuizModuleId(@Param("moduleId") String quizModuleId);
}
