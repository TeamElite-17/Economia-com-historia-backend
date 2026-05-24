package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, String> {
    @Query("SELECT p FROM Participation p WHERE p.user.userId = :userId")
    List<Participation> findByUserId(@Param("userId") String userId);
}
