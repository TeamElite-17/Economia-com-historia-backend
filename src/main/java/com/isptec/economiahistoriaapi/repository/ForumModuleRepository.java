package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ForumModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumModuleRepository extends JpaRepository<ForumModule, String> {
    @Query("SELECT m FROM ForumModule m WHERE m.app.appId = :appId")
    List<ForumModule> findByAppId(@Param("appId") String appId);
}
