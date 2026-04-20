package com.microservice.foodsharepi.Repository;

import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByStatus(MissionStatus status);
}
