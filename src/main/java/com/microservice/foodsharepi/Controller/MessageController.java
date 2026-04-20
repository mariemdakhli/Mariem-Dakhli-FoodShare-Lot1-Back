package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.Entity.Message;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Service.MessageService;
import com.microservice.foodsharepi.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @PostMapping("/send")
    public Message sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content) {

        User sender = userService.getUserById(senderId).orElseThrow();
        User receiver = userService.getUserById(receiverId).orElseThrow();

        return messageService.sendMessage(sender, receiver, content);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<Message> getMessages(@PathVariable Long receiverId) {
        User receiver = userService.getUserById(receiverId).orElseThrow();
        return messageService.getMessagesByReceiver(receiver);
    }
}
