package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, String> {
    @Query("SELECT a FROM AnswerOption a WHERE a.question.questionId = :questionId")
    List<AnswerOption> findByQuestionId(@Param("questionId") String questionId);
}
