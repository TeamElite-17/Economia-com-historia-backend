package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ProfileModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileModuleRepository extends JpaRepository<ProfileModule, String> {
}
