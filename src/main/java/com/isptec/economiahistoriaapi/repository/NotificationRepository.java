package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId")
    List<Notification> findByUserId(@Param("userId") String userId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.read = :read")
    List<Notification> findByUserIdAndRead(@Param("userId") String userId, @Param("read") boolean read);
}
