package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.DTO.MissionDTO;
import com.microservice.foodsharepi.DTO.MissionMatchDTO;
import com.microservice.foodsharepi.DTO.VolunteerMatchDTO;
import com.microservice.foodsharepi.Entity.*;
import com.microservice.foodsharepi.Repository.MissionRepository;
import com.microservice.foodsharepi.Repository.SkillRepository;
import com.microservice.foodsharepi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService implements IMissionService {

    private final SkillRepository skillRepository;
    private final MissionRepository missionRepository;
    private final AssignmentService assignmentService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ==================== CREATE ====================

    public Mission createMission(MissionDTO dto) {

        Mission mission = new Mission();

        mission.setTitle(dto.getTitle());
        mission.setLocation(dto.getLocation());
        mission.setDate(dto.getDate());
        mission.setDuration(dto.getDuration());

        mission.setStatus(parseStatus(dto.getStatus()));

        List<Long> skillIds = dto.getRequiredSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            mission.setRequiredSkills(new ArrayList<>());
        } else {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            mission.setRequiredSkills(skills);
        }

        Mission saved = missionRepository.save(mission);

        try {
            autoAssignMission(saved); // ✅ AUTO MATCH
        } catch (Exception e) {
            System.err.println("Auto-assignment failed: " + e.getMessage());
        }

        return saved;
    }

    // ==================== AUTO ASSIGN ====================

    private void autoAssignMission(Mission mission) {
        System.out.println("=== AUTO ASSIGN MISSION STARTED ===");
        System.out.println("Mission ID: " + mission.getId());

        List<VolunteerMatchDTO> ranked = rankVolunteers(mission.getId());

        if (ranked.isEmpty()) {
            System.out.println("No volunteers found for auto-assignment");
            return;
        }

        // Assign multiple top volunteers (score > 0.5)
        List<VolunteerMatchDTO> topMatches = ranked.stream()
                .filter(v -> v.getScore() > 0.5)
                .toList();
        
        System.out.println("Found " + topMatches.size() + " qualified volunteers (score > 0.5)");

        if (topMatches.isEmpty()) {
            // If no one is above 0.5, assigned the best one anyway if they have some score
            if (ranked.get(0).getScore() > 0) {
                topMatches = List.of(ranked.get(0));
                System.out.println("No one above 0.5, picking the single best match instead.");
            }
        }

        for (VolunteerMatchDTO best : topMatches) {
            try {
                System.out.println("Assigning volunteer: " + best.getFullName() + " with score " + best.getScore());
                assignmentService.assignUserToMission(
                        best.getUserId(),
                        mission.getId(),
                        "VOLUNTEER"
                );

                User user = userRepository.findById(best.getUserId()).orElseThrow();
                emailService.sendSimpleEmail(
                        user.getEmail(),
                        "New Mission Assigned 🚀",
                        "You have been assigned to: " + mission.getTitle()
                );
            } catch (Exception e) {
                System.err.println("Error assigning volunteer " + best.getFullName() + ": " + e.getMessage());
            }
        }

        System.out.println("=== AUTO ASSIGN MISSION COMPLETED ===");
    }

    // ==================== UPDATE ====================

    public Mission updateMission(Long id, MissionDTO dto) {

        Mission mission = getMissionById(id);

        mission.setTitle(dto.getTitle());
        mission.setLocation(dto.getLocation());
        mission.setDate(dto.getDate());
        mission.setDuration(dto.getDuration());

        mission.setStatus(parseStatus(dto.getStatus()));

        List<Long> skillIds = dto.getRequiredSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            mission.setRequiredSkills(new ArrayList<>());
        } else {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            mission.setRequiredSkills(skills);
        }

        Mission updated = missionRepository.save(mission);

        try {
            List<User> admins = userRepository.findByRole(Role.ADMIN);

            for (User admin : admins) {
                notificationService.sendNotification(
                        admin,
                        "Mission updated: " + updated.getTitle()
                );
            }
        } catch (Exception e) {
            System.err.println("Notification failed during mission update: " + e.getMessage());
        }

        return updated;
    }

    // ==================== DELETE ====================

    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    // ==================== GET ====================

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));
    }

    public List<Mission> getByStatus(MissionStatus status) {
        return missionRepository.findByStatus(status);
    }

    // ==================== COMPLETE MISSION (FULL WORKFLOW) ====================

    public Mission completeMission(Long missionId) {

        Mission mission = getMissionById(missionId);

        mission.setStatus(MissionStatus.COMPLETED);
        missionRepository.save(mission);

        List<Assignment> assignments =
                assignmentService.getAssignmentsByMission(missionId);

        for (Assignment a : assignments) {

            User user = a.getUser();

            // 🔥 1. ADD HOURS
            addHours(user, mission);

            // 🔥 2. UPDATE BADGE
            updateBadge(user);

            // 🔥 3. NOTIFY VOLUNTEER
            notificationService.sendNotification(
                    user,
                    "🎉 Mission completed: " + mission.getTitle()
            );
        }

        // 🔥 NOTIFY ADMINS
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            notificationService.sendNotification(
                    admin,
                    "✅ Mission COMPLETED: " + mission.getTitle()
            );
        }

        for (Assignment a : assignments) {

            User user = a.getUser();

            addHours(user, mission);
            updateBadge(user);

            notificationService.sendNotification(
                    user,
                    "🎉 Mission completed: " + mission.getTitle()
            );

            // 🔥 EMAIL COMPLETION
            emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Mission Completed 🎉",
                    "Congrats! You completed: " + mission.getTitle()
            );
        }

        for (User admin : admins) {

            notificationService.sendNotification(
                    admin,
                    "✅ Mission COMPLETED: " + mission.getTitle()
            );

            emailService.sendSimpleEmail(
                    admin.getEmail(),
                    "Mission Completed Report",
                    "Mission finished: " + mission.getTitle()
            );
        }

        return mission;
    }

    // ==================== HOURS ====================

    private void addHours(User user, Mission mission) {

        if (user.getTotalHours() == null) {
            user.setTotalHours(0);
        }

        user.setTotalHours(user.getTotalHours() + mission.getDuration());

        userRepository.save(user);
    }

    // ==================== BADGES ====================

    private void updateBadge(User user) {

        int hours = user.getTotalHours() == null ? 0 : user.getTotalHours();

        if (hours >= 30) {
            user.setBadge(Badge.GOLD);
        } else if (hours >= 10) {
            user.setBadge(Badge.SILVER);
        } else {
            user.setBadge(Badge.BRONZE);
        }

        userRepository.save(user);
    }

    // ==================== MATCH SCORE ====================

    public double calculateMatch(User user, Mission mission) {

        if (mission.getRequiredSkills() == null || mission.getRequiredSkills().isEmpty()) {
            return 1.0; // Everyone is a match if no skills are required
        }

        long matched = user.getSkills().stream()
                .filter(mission.getRequiredSkills()::contains)
                .count();

        return (double) matched / mission.getRequiredSkills().size();
    }

    // ==================== STATUS ====================

    private MissionStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return MissionStatus.PENDING;
        }
        try {
            return MissionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status received: " + status + ". Defaulting to PENDING.");
            return MissionStatus.PENDING;
        }
    }

    public double calculateMatch(Mission mission, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return calculateMatch(user, mission);
    }


    public List<VolunteerMatchDTO> rankVolunteers(Long missionId) {

        Mission mission = getMissionById(missionId);

        System.out.println("=== RANKING VOLUNTEERS FOR MISSION " + missionId + " ===");
        System.out.println("Mission: " + mission.getTitle());
        System.out.println("Required skills: " + (mission.getRequiredSkills() != null ? mission.getRequiredSkills().size() : 0));
        if (mission.getRequiredSkills() != null) {
            mission.getRequiredSkills().forEach(s -> System.out.println("  - " + s.getName()));
        }

        List<User> volunteers = userRepository.findByRole(Role.VOLUNTEER);
        System.out.println("Total volunteers: " + volunteers.size());

        List<VolunteerMatchDTO> result = new ArrayList<>();

        for (User user : volunteers) {

            double skillScore = calculateMatch(user, mission);
            double experienceScore = Math.min((user.getTotalHours() == null ? 0 : user.getTotalHours()) / 100.0, 1.0);
            double availabilityScore = user.isAvailable() ? 1.0 : 0.0;

            double finalScore =
                    (skillScore * 0.6) +
                            (experienceScore * 0.2) +
                            (availabilityScore * 0.2);

            System.out.println("Volunteer: " + user.getFirstName() + " " + user.getLastName() +
                    " | Skills: " + (user.getSkills() != null ? user.getSkills().size() : 0) +
                    " | Available: " + user.isAvailable() +
                    " | SkillScore: " + skillScore +
                    " | ExpScore: " + experienceScore +
                    " | AvailScore: " + availabilityScore +
                    " | FinalScore: " + finalScore);

            result.add(new VolunteerMatchDTO(
                    user.getId(),
                    user.getFirstName() + " " + user.getLastName(),
                    finalScore
            ));
        }

        // sort DESC
        List<VolunteerMatchDTO> sorted = result.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .toList();

        System.out.println("=== BEST MATCH: " + (sorted.isEmpty() ? "NONE" : sorted.get(0).getFullName() + " with score " + sorted.get(0).getScore()) + " ===");

        return sorted;
    }

    @Override
    public List<MissionMatchDTO> rankMissionsForVolunteer(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Mission> missions = missionRepository.findAll().stream()
                .filter(m -> m.getStatus() == MissionStatus.PENDING)                .toList();

        List<MissionMatchDTO> result = new ArrayList<>();

        for (Mission mission : missions) {
            double skillMatch = calculateMatch(user, mission);
            double experienceScore = Math.min((user.getTotalHours() == null ? 0 : user.getTotalHours()) / 100.0, 1.0);
            double availabilityScore = user.isAvailable() ? 1.0 : 0.0;

            double finalScore =
                    (skillMatch * 0.6) +
                    (experienceScore * 0.2) +
                    (availabilityScore * 0.2);

            result.add(new MissionMatchDTO(
                    mission.getId(),
                    mission.getTitle(),
                    mission.getLocation(),
                    mission.getDate(),
                    mission.getDuration(),
                    finalScore
            ));
        }

        return result.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .toList();
    }
}