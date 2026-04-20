package com.microservice.foodsharepi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VolunteerMatchDTO {
    private Long userId;
    private String fullName;
    private double score;
}
