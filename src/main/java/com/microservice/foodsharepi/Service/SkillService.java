package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Mission;
import com.microservice.foodsharepi.Entity.Skill;
import com.microservice.foodsharepi.Entity.User;
import com.microservice.foodsharepi.Repository.MissionRepository;
import com.microservice.foodsharepi.Repository.SkillRepository;
import com.microservice.foodsharepi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService implements ISkillService {

    private final SkillRepository skillRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public void deleteSkill(Long id) {
        // Remove skill from all missions
        List<Mission> missions = missionRepository.findAll();
        for (Mission mission : missions) {
            if (mission.getRequiredSkills() != null) {
                mission.getRequiredSkills().removeIf(skill -> skill.getId().equals(id));
                missionRepository.save(mission);
            }
        }

        // Remove skill from all users
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getSkills() != null) {
                user.getSkills().removeIf(skill -> skill.getId().equals(id));
                userRepository.save(user);
            }
        }

        // Now delete the skill
        skillRepository.deleteById(id);
    }

    public Skill updateSkill(Long id, Skill skill) {
        skill.setId(id);
        return skillRepository.save(skill);
    }
}
