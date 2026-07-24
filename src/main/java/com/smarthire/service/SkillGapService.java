package com.smarthire.service;

import com.smarthire.model.Job;
import com.smarthire.model.Resume;
import com.smarthire.model.SkillGap;
import com.smarthire.repository.SkillGapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SkillGapService {

    @Autowired
    private SkillGapRepository skillGapRepository;

    // Learning resources for each skill
    private static final Map<String, String> LEARNING_RESOURCES = new HashMap<>();

    static {
        LEARNING_RESOURCES.put("SPRING BOOT", "https://spring.io/guides");
        LEARNING_RESOURCES.put("DOCKER", "https://docs.docker.com/get-started");
        LEARNING_RESOURCES.put("REST API", "https://www.javatpoint.com/restful-web-services");
        LEARNING_RESOURCES.put("REACT", "https://react.dev/learn");
        LEARNING_RESOURCES.put("ANGULAR", "https://angular.io/tutorial");
        LEARNING_RESOURCES.put("PYTHON", "https://www.python.org/about/gettingstarted");
        LEARNING_RESOURCES.put("MICROSERVICES", "https://microservices.io");
        LEARNING_RESOURCES.put("MONGODB", "https://www.mongodb.com/docs");
        LEARNING_RESOURCES.put("AWS", "https://aws.amazon.com/getting-started");
        LEARNING_RESOURCES.put("JENKINS", "https://www.jenkins.io/doc/tutorials");
        LEARNING_RESOURCES.put("LINUX", "https://linuxjourney.com");
        LEARNING_RESOURCES.put("GIT", "https://git-scm.com/doc");
        LEARNING_RESOURCES.put("HIBERNATE", "https://hibernate.org/orm/documentation");
        LEARNING_RESOURCES.put("MAVEN", "https://maven.apache.org/guides");
        LEARNING_RESOURCES.put("OOP", "https://www.javatpoint.com/java-oops-concepts");
        LEARNING_RESOURCES.put("BOOTSTRAP", "https://getbootstrap.com/docs");
        LEARNING_RESOURCES.put("PHP", "https://www.php.net/manual/en");
        LEARNING_RESOURCES.put("DATA STRUCTURES", "https://www.geeksforgeeks.org/data-structures");
        LEARNING_RESOURCES.put("JAVASCRIPT", "https://javascript.info");
        LEARNING_RESOURCES.put("MYSQL", "https://dev.mysql.com/doc");
    }

    // Priority based on skill importance
    private SkillGap.Priority getPriority(String skill) {
        List<String> highPriority = Arrays.asList(
            "SPRING BOOT", "REST API", "MICROSERVICES", "DOCKER", "AWS"
        );
        List<String> mediumPriority = Arrays.asList(
            "REACT", "ANGULAR", "MONGODB", "JENKINS", "HIBERNATE"
        );
        if (highPriority.contains(skill.toUpperCase())) {
            return SkillGap.Priority.HIGH;
        } else if (mediumPriority.contains(skill.toUpperCase())) {
            return SkillGap.Priority.MEDIUM;
        }
        return SkillGap.Priority.LOW;
    }

    // Save skill gaps to database
    @Transactional
    public List<SkillGap> saveSkillGaps(Resume resume, Job job,
                                         List<String> missingSkills) {
        // Delete old gaps for this resume+job
        skillGapRepository.deleteByResumeAndJob(resume, job);

        List<SkillGap> gaps = new ArrayList<>();
        for (String skill : missingSkills) {
            String resource = LEARNING_RESOURCES.getOrDefault(
                skill.toUpperCase(), "https://www.google.com/search?q=learn+" + skill
            );
            SkillGap gap = new SkillGap(
                resume, job, skill,
                resource, getPriority(skill)
            );
            gaps.add(skillGapRepository.save(gap));
        }
        return gaps;
    }

    // Get skill gaps for resume and job
    public List<SkillGap> getSkillGaps(Resume resume, Job job) {
        return skillGapRepository.findByResumeAndJob(resume, job);
    }
}