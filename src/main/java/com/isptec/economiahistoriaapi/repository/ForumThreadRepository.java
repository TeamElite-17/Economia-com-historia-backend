package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, String> {
    List<ForumThread> findByForumModuleId(String forumModuleId);
}
