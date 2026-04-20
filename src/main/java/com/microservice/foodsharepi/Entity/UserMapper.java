package com.microservice.foodsharepi.Entity;

import com.microservice.foodsharepi.DTO.UserProfileDTO;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class UserMapper {

    public UserProfileDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        // ✅ Convertir byte[] en String Base64 pour le DTO
        String profileImageBase64 = null;
        if (user.getProfileImage() != null && user.getProfileImage().length > 0) {
            profileImageBase64 = "data:image/jpeg;base64," +
                    Base64.getEncoder().encodeToString(user.getProfileImage());
        }

        return UserProfileDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .available(user.isAvailable())
                .totalHours(user.getTotalHours())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profilePicture(user.getProfilePicture())
                .profileImage(profileImageBase64)  // ✅ String Base64
                .points(user.getPoints())
                .badge(user.getBadge())
                .skills(user.getSkills())
                .fullName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""))
                .initials(getInitials(user.getFirstName(), user.getLastName()))
                .build();
    }

    private String getInitials(String firstName, String lastName) {
        if (firstName == null || lastName == null) return "";
        return (firstName.charAt(0) + "" + lastName.charAt(0)).toUpperCase();
    }
}