package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Skill;

import java.util.List;

public interface ISkillService {
    Skill createSkill(Skill skill);
    List<Skill> getAllSkills();
    void deleteSkill(Long id);
}
