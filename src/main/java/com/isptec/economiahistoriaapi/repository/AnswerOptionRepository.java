package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, String> {
    List<AnswerOption> findByQuestionId(String questionId);
}
