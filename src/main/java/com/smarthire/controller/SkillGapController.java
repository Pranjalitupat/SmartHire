package com.smarthire.controller;

import com.smarthire.model.Job;
import com.smarthire.model.Resume;
import com.smarthire.model.SkillGap;
import com.smarthire.model.User;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.MatchingService;
import com.smarthire.service.SkillGapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class SkillGapController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/skillgap/{jobId}")
    public String showSkillGap(@PathVariable int jobId,
                                Authentication authentication,
                                Model model) {

        // Get logged in user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Get latest resume
        Resume resume = resumeRepository
            .findTopByUserOrderByUploadedAtDesc(user)
            .orElse(null);

        if (resume == null) {
            return "redirect:/upload";
        }

        // Get job
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return "redirect:/match";
        }

        // Get match result for this job
        List<MatchingService.JobMatchResult> allMatches =
            matchingService.matchResumeWithJobs(resume);

        MatchingService.JobMatchResult matchResult = allMatches.stream()
            .filter(m -> m.job.getId() == jobId)
            .findFirst().orElse(null);

        if (matchResult == null) {
            return "redirect:/match";
        }

        // Save and get skill gaps
        List<SkillGap> skillGaps = skillGapService.saveSkillGaps(
            resume, job, matchResult.missingSkills
        );

        model.addAttribute("job", job);
        model.addAttribute("resume", resume);
        model.addAttribute("matchResult", matchResult);
        model.addAttribute("skillGaps", skillGaps);

        return "skillgap";
    }
}