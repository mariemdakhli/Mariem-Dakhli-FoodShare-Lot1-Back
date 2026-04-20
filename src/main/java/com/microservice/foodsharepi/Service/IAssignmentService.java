package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Assignment;
import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.User;

import java.util.List;

public interface IAssignmentService {
    Assignment assignUserToMission(Long userId, Long missionId, String role);
    List<Assignment> getAssignmentsByMission(Long missionId);
    List<User> findMatchingVolunteers(Mission mission);
    List<Assignment> autoAssignMission(Mission mission);
    Mission getMissionById(Long missionId);
    Assignment markAsCompletedByVolunteer(Long assignmentId);
    Assignment verifyAssignment(Long assignmentId);
    List<Assignment> getAssignmentsByUser(Long userId);
    Assignment updateAssignmentStatus(Long assignmentId, String status);

}

