package com.microservice.foodsharepi.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MissionDTO {

    private String title;
    private String location;
    private LocalDate date;
    private int duration;

    private String status; // "PENDING"

    private List<Long> requiredSkillIds;
}