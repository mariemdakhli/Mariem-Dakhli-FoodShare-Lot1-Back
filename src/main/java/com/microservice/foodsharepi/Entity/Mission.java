package com.microservice.foodsharepi.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private LocalDate date;
    private int duration; // en heures

    @Enumerated(EnumType.STRING)
    private MissionStatus status; // PENDING, ASSIGNED, COMPLETED

    // compétences requises
    @ManyToMany
    @JoinTable(
            name = "mission_skills",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> requiredSkills;
}
