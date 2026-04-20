package com.microservice.foodsharepi.DTO;

import com.microservice.foodsharepi.Entity.Badge;
import com.microservice.foodsharepi.Entity.Role;
import com.microservice.foodsharepi.Entity.Skill;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Role role;
    private boolean available;
    private Integer totalHours;
    private boolean enabled;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Photos de profil - Les deux sont des String dans le DTO
    private String profilePicture;  // URL de l'image
    private String profileImage;    // Base64 ou URL (plus de byte[])

    private Integer points;
    private Badge badge;
    private List<Skill> skills;

    private String fullName;
    private String initials;
}