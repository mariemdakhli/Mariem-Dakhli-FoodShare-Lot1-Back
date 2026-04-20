package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;

    public Notification sendNotification(User user, String message) {

        Notification notif = new Notification();
        notif.setUser(user);
        notif.setMessage(message);
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notif);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUser(user);
    }
}
