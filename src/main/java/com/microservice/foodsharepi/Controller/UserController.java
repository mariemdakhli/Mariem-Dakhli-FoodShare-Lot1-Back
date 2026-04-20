package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.DTO.UserProfileDTO;
import com.microservice.foodsharepi.DTO.VolunteerProfileRequest;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Entity.UserMapper;
import com.microservice.foodsharepi.Service.AssignmentService;
import com.microservice.foodsharepi.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final UserMapper userMapper;

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<UserProfileDTO> getAll() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ UNE SEULE MÉTHODE pour GET /{id} - Retourne UserProfileDTO
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // ✅ Méthode alternative pour récupérer l'entité User brute (si nécessaire)
    @GetMapping("/{id}/entity")
    public User getUserEntityById(@PathVariable Long id) {
        return userService.getUserById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/skills")
    public User addSkillsToVolunteer(
            @PathVariable Long id,
            @RequestBody List<Long> skillIds
    ) {
        return userService.addSkillsToVolunteer(id, skillIds);
    }

    @PutMapping("/{id}/volunteer-profile")
    public ResponseEntity<UserProfileDTO> updateVolunteerProfile(
            @PathVariable Long id,
            @RequestBody VolunteerProfileRequest request
    ) {
        User updatedUser = userService.updateVolunteerProfile(id, request);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    // Récupérer le profil de l'utilisateur connecté
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié (Token manquant ou invalide)");
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // Upload photo de profil
    @PostMapping("/profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            String imageUrl = userService.uploadProfilePicture(email, file);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("message", "Image uploadée avec succès");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'upload: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ✅ Endpoint pour mettre à jour le profil général
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestBody UserProfileDTO profileDTO,
            Authentication authentication) {
        String email = authentication.getName();
        User userToUpdate = new User();
        userToUpdate.setFirstName(profileDTO.getFirstName());
        userToUpdate.setLastName(profileDTO.getLastName());
        userToUpdate.setPhoneNumber(profileDTO.getPhoneNumber());
        userToUpdate.setAddress(profileDTO.getAddress());

        User updatedUser = userService.updateProfile(email, userToUpdate);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    // ✅ Récupérer les bénévoles disponibles
    @GetMapping("/available")
    public List<UserProfileDTO> getAvailableVolunteers() {
        return userService.getAvailableVolunteers()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Récupérer le top des bénévoles
    @GetMapping("/top")
    public List<UserProfileDTO> getTopVolunteers() {
        return userService.getTopVolunteers()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}