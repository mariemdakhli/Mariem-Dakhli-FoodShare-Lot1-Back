package com.microservice.foodsharepi.Entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Mission mission;

    private String role; // ex: driver, helper
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
}
