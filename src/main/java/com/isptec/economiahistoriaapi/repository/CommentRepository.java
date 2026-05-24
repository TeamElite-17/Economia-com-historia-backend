package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByAuthorId(String userId);
}
