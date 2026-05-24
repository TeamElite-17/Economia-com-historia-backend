package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.QuizModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizModuleRepository extends JpaRepository<QuizModule, String> {
    @Query("SELECT m FROM QuizModule m WHERE m.app.appId = :appId")
    List<QuizModule> findByAppId(@Param("appId") String appId);
}
