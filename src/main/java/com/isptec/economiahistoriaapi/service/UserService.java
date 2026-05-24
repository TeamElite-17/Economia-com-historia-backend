package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.User;
import com.isptec.economiahistoriaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
    
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
