package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;

import java.util.List;

public interface INotificationService {
    Notification sendNotification(User user, String message);
    List<Notification> getUserNotifications(User user);
}
