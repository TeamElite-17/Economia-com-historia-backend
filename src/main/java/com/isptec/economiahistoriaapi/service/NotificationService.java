package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.model.Notification;
import com.isptec.economiahistoriaapi.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public Optional<Notification> getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }
    
    public List<Notification> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public List<Notification> getUnreadNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
