package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.ForumThread;
import com.isptec.economiahistoriaapi.repository.ForumThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForumThreadService {
    
    private final ForumThreadRepository forumThreadRepository;
    
    public ForumThread createThread(ForumThread thread) {
        return forumThreadRepository.save(thread);
    }
    
    public Optional<ForumThread> getThreadById(String threadId) {
        return forumThreadRepository.findById(threadId);
    }
    
    public List<ForumThread> getThreadsByModule(String forumModuleId) {
        return forumThreadRepository.findByForumModuleId(forumModuleId);
    }

    public List<ForumThread> getThreadsByTopic(String topicId) {
        return forumThreadRepository.findByTopicId(topicId);
    }
    
    public List<ForumThread> getAllThreads() {
        return forumThreadRepository.findAll();
    }
    
    public ForumThread updateThread(ForumThread thread) {
        return forumThreadRepository.save(thread);
    }
    
    public void deleteThread(String threadId) {
        forumThreadRepository.deleteById(threadId);
    }
}
