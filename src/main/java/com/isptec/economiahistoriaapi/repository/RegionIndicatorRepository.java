package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.RegionIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionIndicatorRepository extends JpaRepository<RegionIndicator, String> {
}
