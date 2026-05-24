package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ScoreResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreResultRepository extends JpaRepository<ScoreResult, String> {
    @Query("SELECT s FROM ScoreResult s WHERE s.quiz.quizId = :quizId")
    List<ScoreResult> findByQuizId(@Param("quizId") String quizId);
}
