package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, String> {
    List<Participation> findByUserId(String userId);
}
