package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Message;
import com.microservice.foodsharepi.Entity.Notification;
import com.microservice.foodsharepi.Entity.User;

import java.util.List;

public interface IMessageService {
    List<Message> getMessagesByReceiver(User receiver);

    Message sendMessage(User sender, User receiver, String content);
}
