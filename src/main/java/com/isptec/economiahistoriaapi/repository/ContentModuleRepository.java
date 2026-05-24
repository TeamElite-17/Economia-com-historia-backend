package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ContentModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentModuleRepository extends JpaRepository<ContentModule, String> {
    @Query("SELECT m FROM ContentModule m WHERE m.app.appId = :appId")
    List<ContentModule> findByAppId(@Param("appId") String appId);
}
