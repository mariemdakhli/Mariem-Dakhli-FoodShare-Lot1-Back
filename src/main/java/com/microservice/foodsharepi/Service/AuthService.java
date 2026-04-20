package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.AuthResponse;
import com.microservice.foodsharepi.DTO.RegisterVolunteerRequest;
import com.microservice.foodsharepi.DTO.SetPasswordRequest;
import com.microservice.foodsharepi.DTO.UserResponse;
import com.microservice.foodsharepi.Security.JwtUtil;
import com.microservice.foodsharepi.Entity.Role;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;



    public String register(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.VOLUNTEER);
        }

        if (user.getRole() == Role.ADMIN) {
            user.setEnabled(true);
            user.setPasswordSet(true);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "User registered successfully";
    }

    /**
     * ✅ Inscription d'un bénévole SANS mot de passe
     * Un email est envoyé pour créer le mot de passe
     */
    public String registerVolunteer(RegisterVolunteerRequest dto) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Créer le bénévole
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());

        // ✅ PAS de mot de passe à l'inscription
        user.setPassword(null);
        user.setPasswordSet(false);

        // ✅ Générer un token pour la création du mot de passe
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(24)); // Valide 24h

        // Rôle et statuts
        user.setRole(Role.VOLUNTEER);
        user.setEnabled(true);
        user.setAvailable(true);
        user.setEmailVerified(false);
        user.setTotalHours(0);
        user.setPoints(0);
        user.setBadge(null);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // ✅ Envoyer l'email de bienvenue avec lien de création de mot de passe
        emailService.sendWelcomeAndSetPasswordEmail(
                dto.getEmail(),
                dto.getFirstName(),
                resetToken
        );

        return "Inscription réussie ! Un email vous a été envoyé pour créer votre mot de passe.";
    }

    /**
     * ✅ Définir le mot de passe avec le token reçu par email
     */
    public String setPassword(SetPasswordRequest request) {
        // Vérifier que les mots de passe correspondent
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        // Vérifier la force du mot de passe
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Trouver l'utilisateur par token
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Lien invalide ou expiré"));

        // Vérifier que le token n'a pas expiré
        if (user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien a expiré. Veuillez demander un nouveau lien.");
        }

        // Définir le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordSet(true);
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        user.setEmailVerified(true); // ✅ L'email est vérifié

        userRepository.save(user);

        return "Mot de passe créé avec succès ! Vous pouvez maintenant vous connecter.";
    }

    /**
     * ✅ Vérifier la validité du token
     */
    public boolean isValidResetToken(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(user -> user.getResetPasswordExpiry().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    /**
     * ✅ Renvoyer l'email de création de mot de passe
     */
    public String resendSetPasswordEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec cet email"));

        if (user.isPasswordSet()) {
            throw new RuntimeException("Le mot de passe est déjà défini pour ce compte");
        }

        // Générer un nouveau token
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Envoyer l'email
        emailService.sendWelcomeAndSetPasswordEmail(
                user.getEmail(),
                user.getFirstName(),
                resetToken
        );

        return "Un nouvel email a été envoyé.";
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        // ✅ Vérifier si le mot de passe a été défini
        if (!user.isPasswordSet()) {
            throw new RuntimeException("Votre mot de passe n'a pas encore été défini. Veuillez vérifier vos emails.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());
        userResponse.setPoints(user.getPoints());
        userResponse.setTotalHours(user.getTotalHours());
        userResponse.setBadge(user.getBadge() != null ? user.getBadge().name() : null);
        userResponse.setAvailable(user.isAvailable());

        return new AuthResponse(token, userResponse);
    }

    public UserResponse getCurrentUser(String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setPoints(user.getPoints());
        response.setTotalHours(user.getTotalHours());
        response.setBadge(user.getBadge() != null ? user.getBadge().name() : null);
        response.setAvailable(user.isAvailable());

        return response;
    }
}