package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.Post;
import com.isptec.economiahistoriaapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
    
    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }
    
    public List<Post> getPostsByThread(String threadId) {
        return postRepository.findByForumThreadId(threadId);
    }
    
    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByAuthorId(userId);
    }
    
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }
    
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }
}
