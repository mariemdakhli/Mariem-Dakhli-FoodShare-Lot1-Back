package com.microservice.foodsharepi.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime sentAt;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;
}