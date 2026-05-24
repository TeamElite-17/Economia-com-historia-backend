package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.Comment;
import com.isptec.economiahistoriaapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }
    
    public List<Comment> getCommentsByPost(String postId) {
        return commentRepository.findByPostId(postId);
    }
    
    public List<Comment> getCommentsByUser(String userId) {
        return commentRepository.findByAuthorId(userId);
    }
    
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }
}
