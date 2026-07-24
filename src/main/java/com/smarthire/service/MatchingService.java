package com.smarthire.service;

import com.smarthire.model.Job;
import com.smarthire.model.Resume;
import com.smarthire.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchingService {

    @Autowired
    private JobRepository jobRepository;

    // Match result class
    public static class JobMatchResult {
        public Job job;
        public double matchScore;
        public List<String> matchedSkills;
        public List<String> missingSkills;
        public String matchLabel;

        public JobMatchResult(Job job, double matchScore,
                            List<String> matchedSkills,
                            List<String> missingSkills) {
            this.job = job;
            this.matchScore = matchScore;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.matchLabel = getLabel(matchScore);
        }

        private String getLabel(double score) {
            if (score >= 80) return "STRONG MATCH";
            if (score >= 60) return "GOOD MATCH";
            if (score >= 40) return "AVERAGE MATCH";
            return "WEAK MATCH";
        }
    }

    // Main matching method
    public List<JobMatchResult> matchResumeWithJobs(Resume resume) {

        // Get resume skills
        String resumeSkillsStr = resume.getExtractedSkills();
        if (resumeSkillsStr == null || resumeSkillsStr.isEmpty()) {
            return new ArrayList<>();
        }

        // Convert resume skills to list
        List<String> resumeSkills = Arrays.asList(
            resumeSkillsStr.toLowerCase().split(",\\s*")
        );

        // Get all active jobs
        List<Job> allJobs = jobRepository.findByIsActiveTrue();
        List<JobMatchResult> results = new ArrayList<>();

        for (Job job : allJobs) {
            if (job.getRequiredSkills() == null) continue;

            // Get job required skills
            List<String> jobSkills = Arrays.asList(
                job.getRequiredSkills().toLowerCase().split(",\\s*")
            );

            // Find matched skills
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String skill : jobSkills) {
                if (resumeSkills.contains(skill.trim())) {
                    matched.add(skill.trim().toUpperCase());
                } else {
                    missing.add(skill.trim().toUpperCase());
                }
            }

            // Calculate match score
            double score = 0;
            if (!jobSkills.isEmpty()) {
                score = ((double) matched.size() / jobSkills.size()) * 100;
                score = Math.round(score * 100.0) / 100.0;
            }

            results.add(new JobMatchResult(job, score, matched, missing));
        }

        // Sort by score descending
        results.sort((a, b) -> Double.compare(b.matchScore, a.matchScore));

        // Return top 5
        return results.subList(0, Math.min(5, results.size()));
    }
}