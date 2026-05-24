package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ForumModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumModuleRepository extends JpaRepository<ForumModule, String> {
    List<ForumModule> findByAppId(String appId);
}
