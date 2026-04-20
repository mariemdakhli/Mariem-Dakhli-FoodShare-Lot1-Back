package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * Envoyer un email de test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestParam String to) {
        try {
            emailService.sendWelcomeAndSetPasswordEmail(to, "Test User", "test-token-123");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email de test envoyé à : " + to
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }


    /**
     * Envoyer un email simple
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {
        try {
            emailService.sendSimpleEmail(to, subject, text);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email envoyé avec succès"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}