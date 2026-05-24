package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId")
    List<Comment> findByPostId(@Param("postId") String postId);

    @Query("SELECT c FROM Comment c WHERE c.author.userId = :userId")
    List<Comment> findByAuthorId(@Param("userId") String userId);
}
