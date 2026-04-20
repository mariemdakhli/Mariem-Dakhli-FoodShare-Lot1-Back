package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.DTO.MissionDTO;
import com.microservice.foodsharepi.DTO.MissionMatchDTO;
import com.microservice.foodsharepi.DTO.VolunteerMatchDTO;
import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.MissionStatus;
import com.microservice.foodsharepi.Service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MissionController {

    private final MissionService missionService;

    // ==================== CREATE ====================
    @PostMapping
    public Mission create(@RequestBody MissionDTO dto) {
        return missionService.createMission(dto);
    }

    // ==================== GET ALL ====================
    @GetMapping
    public List<Mission> getAll() {
        return missionService.getAllMissions();
    }

    // ==================== GET BY ID ====================
    @GetMapping("/{id}")
    public Mission getById(@PathVariable Long id) {
        return missionService.getMissionById(id);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public Mission update(@PathVariable Long id, @RequestBody MissionDTO dto) {
        return missionService.updateMission(id, dto);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        missionService.deleteMission(id);
    }

    // ==================== FILTER BY STATUS ====================
    @GetMapping("/status/{status}")
    public List<Mission> getByStatus(@PathVariable String status) {
        return missionService.getByStatus(
                MissionStatus.valueOf(status.toUpperCase())
        );
    }

    // ==================== COMPLETE MISSION (FULL WORKFLOW) ====================
    @PutMapping("/{id}/complete")
    public Mission complete(@PathVariable Long id) {
        return missionService.completeMission(id);
    }

    // ==================== MATCHING (OPTIONNEL API FRONT) ====================
    @GetMapping("/{id}/match-score")
    public double matchScore(@PathVariable Long id,
                             @RequestParam Long userId) {

        return missionService.calculateMatch(
                missionService.getMissionById(id),
                userId
        );
    }

    @GetMapping("/{id}/ranking")
    public List<VolunteerMatchDTO> getRanking(@PathVariable Long id) {
        return missionService.rankVolunteers(id);
    }

    @GetMapping("/ranking/volunteer/{userId}")
    public List<MissionMatchDTO> getRecommendedMissions(@PathVariable Long userId) {
        return missionService.rankMissionsForVolunteer(userId);
    }
}