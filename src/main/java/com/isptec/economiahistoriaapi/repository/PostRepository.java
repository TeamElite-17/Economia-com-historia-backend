package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author WHERE p.forumThread.threadId = :threadId ORDER BY p.postedAt ASC")
    List<Post> findByForumThreadId(@Param("threadId") String forumThreadId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author WHERE p.author.userId = :userId ORDER BY p.postedAt DESC")
    List<Post> findByAuthorId(@Param("userId") String userId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.forumThread.threadId = :threadId")
    Long countByForumThreadId(@Param("threadId") String threadId);
}

