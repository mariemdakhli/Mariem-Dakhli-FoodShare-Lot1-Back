package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Service.NotificationService;
import com.microservice.foodsharepi.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/send")
    public Notification send(
            @RequestParam Long userId,
            @RequestParam String message) {

        User user = userService.getUserById(userId).orElseThrow();
        return notificationService.sendNotification(user, message);
    }

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        User user = userService.getUserById(userId).orElseThrow();
        return notificationService.getUserNotifications(user);
    }
}