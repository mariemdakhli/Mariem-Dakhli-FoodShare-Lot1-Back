package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.DTO.VolunteerProfileRequest;
import com.microservice.foodsharepi.Entity.Assignment;
import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/assign")
    public Assignment assign(
            @RequestParam Long userId,
            @RequestParam Long missionId,
            @RequestParam String role) {

        return assignmentService.assignUserToMission(userId, missionId, role);
    }

    @GetMapping("/mission/{missionId}")
    public List<Assignment> getByMission(@PathVariable Long missionId) {
        return assignmentService.getAssignmentsByMission(missionId);
    }

    @PostMapping("/auto-assign/{missionId}")
    public List<Assignment> autoAssign(@PathVariable Long missionId) {
        return assignmentService.autoAssignMission(
                assignmentService.getMissionById(missionId)
        );
    }

    @GetMapping("/mission/{missionId}/matches")
    public List<User> getMatchingVolunteers(@PathVariable Long missionId) {
        Mission mission = assignmentService.getMissionById(missionId);
        return assignmentService.findMatchingVolunteers(mission);
    }

    @GetMapping("/user/{userId}")
    public List<Assignment> getByUser(@PathVariable Long userId) {
        return assignmentService.getAssignmentsByUser(userId);
    }


    @PutMapping("/{assignmentId}/complete")
    public Assignment volunteerComplete(@PathVariable Long assignmentId) {
        return assignmentService.markAsCompletedByVolunteer(assignmentId);
    }


    @PutMapping("/{assignmentId}/verify")
    public Assignment verifyAssignment(@PathVariable Long assignmentId) {
        return assignmentService.verifyAssignment(assignmentId);
    }

    @PutMapping("/{assignmentId}/status")
    public Assignment updateAssignmentStatus(
            @PathVariable Long assignmentId,
            @RequestBody StatusRequest statusRequest) {
        return assignmentService.updateAssignmentStatus(assignmentId, statusRequest.getStatus());
    }

    // DTO pour la mise à jour du statut
    public static class StatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @GetMapping
    public List<Assignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }


}
