package com.microservice.foodsharepi.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)  // ✅ Peut être null pour les bénévoles invités
    private String password;

    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean available = true;
    private Integer totalHours = 0;
    private boolean enabled = true;
    private boolean emailVerified = false;

    @Column(nullable = false)
    private Integer points = 0;

    // ✅ Champs pour la gestion du mot de passe
    private boolean passwordSet = false;  // ✅ Indique si le mot de passe a été défini

    private String resetPasswordToken;    // ✅ Token pour réinitialiser/créer le mot de passe

    private LocalDateTime resetPasswordExpiry;  // ✅ Date d'expiration du token

    // Photo de profil
    private String profilePicture;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Badge badge = Badge.BRONZE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Fetch(FetchMode.SUBSELECT) // ← add this import: org.hibernate.annotations.Fetch/FetchMode
    private List<Skill> skills = new java.util.ArrayList<>(); // ← initialize to avoid null

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}