package com.microservice.foodsharepi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionMatchDTO {
    private Long id;
    private String title;
    private String location;
    private LocalDate date;
    private int duration;
    private double score;
}
