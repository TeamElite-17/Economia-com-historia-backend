package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.QuizModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizModuleRepository extends JpaRepository<QuizModule, String> {
    List<QuizModule> findByAppId(String appId);
}
