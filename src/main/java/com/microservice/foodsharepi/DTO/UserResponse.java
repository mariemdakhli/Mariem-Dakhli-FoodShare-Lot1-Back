package com.microservice.foodsharepi.DTO;

import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Integer points;
    private Integer totalHours;
    private String badge;
    private Boolean available;
}