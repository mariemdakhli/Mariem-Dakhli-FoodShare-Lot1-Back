package com.microservice.foodsharepi.Repository;

import com.microservice.foodsharepi.Entity.Assignment;
import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByUser(User user);

    List<Assignment> findByMission(Mission mission);
}
