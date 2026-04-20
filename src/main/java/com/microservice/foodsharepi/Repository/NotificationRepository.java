package com.microservice.foodsharepi.Repository;

import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsReadFalse(User user);
}