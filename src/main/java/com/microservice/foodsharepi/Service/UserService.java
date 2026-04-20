package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.VolunteerProfileRequest;
import com.microservice.foodsharepi.Entity.Role;
import com.microservice.foodsharepi.Entity.Skill;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Repository.SkillRepository;
import com.microservice.foodsharepi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    // ✅ Correction : Utiliser org.springframework.beans.factory.annotation.Value
    @Value("${file.upload-dir:uploads/profiles}")
    private String uploadDir;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setAvailable(updatedUser.isAvailable());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAvailableVolunteers() {
        return userRepository.findByAvailableTrue();
    }

    public List<User> getByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getTopVolunteers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.VOLUNTEER)
                .sorted((a, b) -> Integer.compare(
                        b.getTotalHours() == null ? 0 : b.getTotalHours(),
                        a.getTotalHours() == null ? 0 : a.getTotalHours()
                ))
                .toList();
    }

    public User addSkillsToVolunteer(Long userId, List<Long> skillIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Skill> skills = skillRepository.findAllById(skillIds);
        user.setSkills(skills);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateVolunteerProfile(Long userId, VolunteerProfileRequest request) {
        try {
            System.out.println("Updating volunteer profile for user: " + userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setAvailable(request.isAvailable());
            
            // Update general info if provided
            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) user.setAddress(request.getAddress());

            System.out.println("Updated general info for user " + userId);

            // Update skills if provided
            if (request.getSkillIds() != null) {
                if (request.getSkillIds().isEmpty()) {
                    System.out.println("Clearing skills for user " + userId);
                    user.setSkills(new java.util.ArrayList<>());
                } else {
                    List<Skill> skills = skillRepository.findAllById(request.getSkillIds());
                    System.out.println("Found " + skills.size() + " skills for user " + userId);
                    user.setSkills(skills);
                }
            }

            // Defensive checks for mandatory fields
            if (user.getPoints() == null) {
                System.out.println("Points was null for user " + userId + ". Initializing to 0.");
                user.setPoints(0);
            }
            if (user.getTotalHours() == null) {
                System.out.println("TotalHours was null for user " + userId + ". Initializing to 0.");
                user.setTotalHours(0);
            }

            System.out.println("Applying save to database for user " + userId);
            User saved = userRepository.save(user);
            System.out.println("User " + userId + " updated successfully");
            return saved;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR updating volunteer profile for user " + userId);
            e.printStackTrace();
            throw new RuntimeException("Error updating volunteer profile: " + e.getMessage(), e);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    /**
     * Upload de la photo de profil
     */
    public String uploadProfilePicture(String email, MultipartFile file) {
        try {
            User user = findByEmail(email);

            // Vérifier que le fichier n'est pas vide
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Le fichier est vide");
            }

            // ✅ Utiliser java.nio.file.Path (pas jakarta.persistence.criteria.Path)
            Path uploadPath = Paths.get(uploadDir);

            // Créer le dossier s'il n'existe pas
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Chemin complet du fichier
            Path filePath = uploadPath.resolve(fileName);

            // ✅ Sauvegarder le fichier avec gestion d'IOException
            try {
                Files.copy(file.getInputStream(), filePath);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la copie du fichier: " + e.getMessage(), e);
            }

            // URL de l'image
            String imageUrl = "/" + uploadDir + "/" + fileName;

            // Mettre à jour l'utilisateur
            user.setProfilePicture(imageUrl);
            userRepository.save(user);

            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image: " + e.getMessage(), e);
        }
    }

    /**
     * Upload de la photo de profil en Base64 (Alternative)
     */
    /**
     * Upload de la photo de profil en Base64 (Alternative)
     */
    public String uploadProfilePictureBase64(String email, MultipartFile file) {
        try {
            User user = findByEmail(email);

            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Le fichier est vide");
            }

            // Convertir en Base64
            byte[] bytes = file.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(bytes);
            String imageUrl = "data:" + file.getContentType() + ";base64," + base64Image;

            // ✅ Sauvegarder l'URL dans profilePicture
            user.setProfilePicture(imageUrl);

            // ✅ Sauvegarder les bytes dans profileImageData (pas profileImage)
            user.setProfileImage(bytes);  // ✅ Le champ s'appelle profileImage
            userRepository.save(user);

            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image: " + e.getMessage(), e);
        }
    }

    public User updateProfile(String email, User updatedUser) {
        User user = findByEmail(email);

        // Mettre à jour les champs modifiables
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getPhoneNumber() != null) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        if (updatedUser.getAddress() != null) {
            user.setAddress(updatedUser.getAddress());
        }

        return userRepository.save(user);
    }


}