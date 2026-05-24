package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, String> {
    @Query("SELECT t FROM ForumThread t WHERE t.forumModule.moduleId = :moduleId")
    List<ForumThread> findByForumModuleId(@Param("moduleId") String forumModuleId);
}
