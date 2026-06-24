package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.post.postId = :postId ORDER BY c.commentedAt ASC")
    List<Comment> findByPostId(@Param("postId") String postId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.parentComment.commentId = :parentCommentId ORDER BY c.commentedAt ASC")
    List<Comment> findByParentCommentId(@Param("parentCommentId") String parentCommentId);

    /** Busca comentário com autor já carregado */
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.commentId = :commentId")
    java.util.Optional<Comment> findByIdWithAuthor(@Param("commentId") String commentId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.author.userId = :userId ORDER BY c.commentedAt DESC")
    List<Comment> findByAuthorId(@Param("userId") String userId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.contentItem.contentId = :contentItemId ORDER BY c.commentedAt ASC")
    List<Comment> findByContentItemId(@Param("contentItemId") String contentItemId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.contentItem.contentId = :contentId")
    void deleteByContentItemId(@Param("contentId") String contentId);
}

