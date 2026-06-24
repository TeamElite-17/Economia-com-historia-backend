package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, String> {

    @Query("SELECT t FROM ForumThread t WHERE t.topic.topicId = :topicId")
    List<ForumThread> findByTopicId(@Param("topicId") String topicId);

    /** Busca thread com criador já carregado (evita LazyInitializationException ao gerar notificações) */
    @Query("SELECT t FROM ForumThread t LEFT JOIN FETCH t.createdByUser WHERE t.threadId = :threadId")
    java.util.Optional<ForumThread> findByIdWithCreator(@Param("threadId") String threadId);
}
