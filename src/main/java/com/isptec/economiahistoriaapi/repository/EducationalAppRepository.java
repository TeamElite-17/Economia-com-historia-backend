package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.EducationalApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationalAppRepository extends JpaRepository<EducationalApp, String> {
}
