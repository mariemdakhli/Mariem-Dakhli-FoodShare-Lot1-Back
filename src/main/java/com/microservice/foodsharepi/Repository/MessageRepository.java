package com.microservice.foodsharepi.Repository;

import com.microservice.foodsharepi.Entity.Message;
import com.microservice.foodsharepi.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySender(User sender);

    List<Message> findByReceiver(User receiver);
}
