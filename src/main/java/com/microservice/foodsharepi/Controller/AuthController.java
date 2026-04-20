package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.DTO.AuthResponse;
import com.microservice.foodsharepi.DTO.LoginRequest;
import com.microservice.foodsharepi.DTO.RegisterVolunteerRequest;
import com.microservice.foodsharepi.DTO.SetPasswordRequest;
import com.microservice.foodsharepi.DTO.UserResponse;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription classique (Admin ou avec mot de passe)
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            String message = authService.register(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Connexion
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(
                    request.getEmail(),
                    request.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Inscription d'un bénévole SANS mot de passe
     * Un email est envoyé pour créer le mot de passe
     */
    @PostMapping("/register/volunteer")
    public ResponseEntity<Map<String, Object>> registerVolunteer(@RequestBody RegisterVolunteerRequest dto) {
        try {
            String message = authService.registerVolunteer(dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * ✅ Définir le mot de passe avec le token reçu par email
     */
    @PostMapping("/set-password")
    public ResponseEntity<Map<String, Object>> setPassword(@RequestBody SetPasswordRequest request) {
        try {
            String message = authService.setPassword(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * ✅ Vérifier si le token de réinitialisation est valide
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, Object>> validateResetToken(@RequestParam String token) {
        boolean isValid = authService.isValidResetToken(token);
        return ResponseEntity.ok(Map.of(
                "valid", isValid
        ));
    }

    /**
     * ✅ Renvoyer l'email de création de mot de passe
     */
    @PostMapping("/resend-set-password")
    public ResponseEntity<Map<String, Object>> resendSetPasswordEmail(@RequestBody Map<String, String> request) {
        try {
            String message = authService.resendSetPasswordEmail(request.get("email"));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * ✅ Récupérer l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            UserResponse user = authService.getCurrentUser(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * ✅ Déconnexion (optionnel - juste pour la forme)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Le token est géré côté client (supprimé du localStorage)
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Déconnexion réussie"
        ));
    }
}