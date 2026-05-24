package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.userId = :userId")
    List<QuizAttempt> findByUserId(@Param("userId") String userId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.quizId = :quizId")
    List<QuizAttempt> findByQuizId(@Param("quizId") String quizId);
}
