package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Message;
import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, String content) {

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiver(receiver);
    }


}
