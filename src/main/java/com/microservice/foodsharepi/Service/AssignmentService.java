package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.VolunteerProfileRequest;
import com.microservice.foodsharepi.Entity.*;
import com.microservice.foodsharepi.Repository.SkillRepository;
import com.microservice.foodsharepi.Security.JwtUtil;
import com.microservice.foodsharepi.Repository.AssignmentRepository;
import com.microservice.foodsharepi.Repository.MissionRepository;
import com.microservice.foodsharepi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SkillRepository skillRepository;

    public Assignment assignUserToMission(Long userId, Long missionId, String role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        Assignment assignment = new Assignment();
        assignment.setUser(user);
        assignment.setMission(mission);
        assignment.setRole(role);

        mission.setStatus(MissionStatus.ASSIGNED);
        missionRepository.save(mission); // 🔥 FIX

        Assignment saved = assignmentRepository.save(assignment);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "New Mission Assigned",
                "Mission: " + mission.getTitle()
        );

        notificationService.sendNotification(
                user,
                "Assigned to mission: " + mission.getTitle()
        );

        return saved;
    }

    public List<Assignment> getAssignmentsByMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId).orElseThrow();
        return assignmentRepository.findByMission(mission);
    }

    // ==================== MATCHING AUTOMATIQUE ====================

    public List<User> findMatchingVolunteers(Mission mission) {

        List<User> volunteers = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().name().equals("VOLUNTEER"))
                .toList();

        return volunteers.stream()
                .filter(v -> v.getSkills() != null &&
                        v.getSkills().containsAll(mission.getRequiredSkills()))
                .toList();
    }



    public List<Assignment> autoAssignMission(Mission mission) {

        List<User> matchedVolunteers = findMatchingVolunteers(mission);

        List<Assignment> assignments = matchedVolunteers.stream()
                .map(user -> {

                    Assignment assignment = new Assignment();
                    assignment.setUser(user);
                    assignment.setMission(mission);
                    assignment.setRole("VOLUNTEER");

                    // 🔥 NOTIFICATION DB (IMPORTANT)
                    notificationService.sendNotification(
                            user,
                            "You have been assigned to mission: " + mission.getTitle()
                    );

                    return assignment;
                })
                .toList();

        mission.setStatus(MissionStatus.ASSIGNED);
        missionRepository.save(mission);

        return assignmentRepository.saveAll(assignments);
    }

    public Mission getMissionById(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));
    }

    public List<Assignment> getAssignmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return assignmentRepository.findByUser(user);
    }

    @Transactional
    public Assignment markAsCompletedByVolunteer(Long assignmentId) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // ✅ récupérer mission et user
        Mission mission = assignment.getMission();
        User volunteer = assignment.getUser();

        assignment.setStatus(AssignmentStatus.COMPLETED_BY_VOLUNTEER);

        Assignment saved = assignmentRepository.save(assignment);

        // =========================
        // POINTS FOR VOLUNTEER COMPLETION ⭐
        // =========================
        if (volunteer.getPoints() == null) {
            volunteer.setPoints(0);
        }

        int completionPoints = mission.getDuration() * 5; // 5 points per hour for completion
        volunteer.setPoints(volunteer.getPoints() + completionPoints);

        // =========================
        // UPDATE TOTAL HOURS
        // =========================
        if (volunteer.getTotalHours() == null) {
            volunteer.setTotalHours(0);
        }

        volunteer.setTotalHours(
                volunteer.getTotalHours() + mission.getDuration()
        );

        // =========================
        // UPDATE BADGE
        // =========================
        int hours = volunteer.getTotalHours();

        if (hours >= 30) {
            volunteer.setBadge(Badge.GOLD);
        } else if (hours >= 10) {
            volunteer.setBadge(Badge.SILVER);
        } else {
            volunteer.setBadge(Badge.BRONZE);
        }

        // =========================
        // SAVE USER WITH POINTS
        // =========================
        userRepository.save(volunteer);

        // ✅ récupérer les admins
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {

            emailService.sendSimpleEmail(
                    admin.getEmail(),
                    "Mission completed pending verification",
                    "Volunteer " + volunteer.getFirstName()
                            + " completed mission: " + mission.getTitle()
                            + "\nPoints earned: " + completionPoints
            );
        }

        return saved;
    }
    @Transactional

    public Assignment verifyAssignment(Long assignmentId) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(AssignmentStatus.VERIFIED_BY_ADMIN);

        Assignment saved = assignmentRepository.save(assignment);

        // 👇 USER + MISSION
        User volunteer = saved.getUser();
        Mission mission = saved.getMission();

        // =========================
        // 1. UPDATE MISSION STATUS
        // =========================
        mission.setStatus(MissionStatus.COMPLETED);
        missionRepository.save(mission);

        // =========================
        // 2. HOURS
        // =========================
        if (volunteer.getTotalHours() == null) {
            volunteer.setTotalHours(0);
        }

        volunteer.setTotalHours(
                volunteer.getTotalHours() + mission.getDuration()
        );

        // =========================
        // 3. POINTS ⭐ (FIXED)
        // =========================
        if (volunteer.getPoints() == null) {
            volunteer.setPoints(0);
        }

        int newPoints = mission.getDuration() * 10;
        volunteer.setPoints(volunteer.getPoints() + newPoints);

        // =========================
        // 4. BADGE
        // =========================
        int hours = volunteer.getTotalHours();

        if (hours >= 30) {
            volunteer.setBadge(Badge.GOLD);
        } else if (hours >= 10) {
            volunteer.setBadge(Badge.SILVER);
        } else {
            volunteer.setBadge(Badge.BRONZE);
        }

        // =========================
        // SAVE USER
        // =========================
        userRepository.save(volunteer);

        // =========================
        // EMAIL
        // =========================
        emailService.sendSimpleEmail(
                volunteer.getEmail(),
                "🎉 Mission Verified - FoodShare",
                "Hello " + volunteer.getFirstName() + ",\n\n" +
                        "Your mission has been verified by admin.\n\n" +
                        "Mission: " + mission.getTitle() + "\n" +
                        "Hours gained: " + mission.getDuration() + "\n" +
                        "Points earned: " + newPoints + "\n" +
                        "Total hours: " + volunteer.getTotalHours() + "\n" +
                        "Total points: " + volunteer.getPoints() + "\n" +
                        "Current badge: " + volunteer.getBadge() + "\n\n" +
                        "Thank you ❤️"
        );

        return saved;
    }
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }


}


