package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.VolunteerProfileRequest;
import com.microservice.foodsharepi.Entity.Role;
import com.microservice.foodsharepi.Entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User createUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);
    List<User> getAvailableVolunteers();
    List<User> getByRole(Role role);
    List<User> getTopVolunteers();
    User updateVolunteerProfile(Long userId, VolunteerProfileRequest request);

    User updateProfile(String email, User updatedUser);
    String uploadProfilePictureBase64(String email, MultipartFile file);
    String uploadProfilePicture(String email, MultipartFile file);
}
