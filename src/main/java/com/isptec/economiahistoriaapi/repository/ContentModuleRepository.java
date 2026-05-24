package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ContentModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentModuleRepository extends JpaRepository<ContentModule, String> {
    List<ContentModule> findByAppId(String appId);
}
