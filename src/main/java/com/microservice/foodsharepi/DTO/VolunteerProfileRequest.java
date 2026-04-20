package com.microservice.foodsharepi.DTO;

import lombok.Data;

import java.util.List;

@Data
public class VolunteerProfileRequest {
    private List<Long> skillIds;
    private boolean available;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}
