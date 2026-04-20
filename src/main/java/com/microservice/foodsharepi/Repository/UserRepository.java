package com.microservice.foodsharepi.Repository;

import com.microservice.foodsharepi.Entity.Role;
import com.microservice.foodsharepi.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByAvailableTrue();
    // ✅ Ajouter cette méthode
    Optional<User> findByResetPasswordToken(String token);



    long countByRole(Role role);

}
