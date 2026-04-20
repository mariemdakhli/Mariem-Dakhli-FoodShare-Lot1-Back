package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.MissionDTO;
import com.microservice.foodsharepi.DTO.MissionMatchDTO;
import com.microservice.foodsharepi.DTO.VolunteerMatchDTO;
import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.MissionStatus;
import com.microservice.foodsharepi.Entity.User;

import java.util.List;

public interface IMissionService {
    Mission createMission(MissionDTO dto);
    List<Mission> getAllMissions();
    Mission getMissionById(Long id);
    Mission updateMission(Long id, MissionDTO dto);
    void deleteMission(Long id);
    List<Mission> getByStatus(MissionStatus status);
    Mission completeMission(Long missionId);
    double calculateMatch(User user, Mission mission);
    List<VolunteerMatchDTO> rankVolunteers(Long missionId);
    List<MissionMatchDTO> rankMissionsForVolunteer(Long userId);
}
