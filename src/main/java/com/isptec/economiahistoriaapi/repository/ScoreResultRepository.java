package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ScoreResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreResultRepository extends JpaRepository<ScoreResult, String> {
    List<ScoreResult> findByQuizId(String quizId);
}
