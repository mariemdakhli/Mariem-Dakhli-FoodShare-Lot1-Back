package com.microservice.foodsharepi.Controller;

import com.microservice.foodsharepi.Entity.Skill;
import com.microservice.foodsharepi.Service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/skills")
@RestController
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public Skill create(@RequestBody Skill skill) {
        return skillService.createSkill(skill);
    }

    @GetMapping
    public List<Skill> getAll() {
        return skillService.getAllSkills();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }

    @PutMapping("/{id}")
    public Skill update(@PathVariable Long id, @RequestBody Skill skill) {
        return skillService.updateSkill(id, skill);
    }
}
