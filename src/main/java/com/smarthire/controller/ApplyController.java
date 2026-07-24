package com.smarthire.controller;

import com.smarthire.model.*;
import com.smarthire.repository.*;
import com.smarthire.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ApplyController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/apply/{jobId}")
    public String applyForJob(@PathVariable int jobId,
                               Authentication authentication,
                               Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Get job
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return "redirect:/match";

        // Get latest resume
        Resume resume = resumeRepository
            .findTopByUserOrderByUploadedAtDesc(user)
            .orElse(null);

        if (resume == null) {
            return "redirect:/upload";
        }

        // Check already applied
        if (jobApplicationRepository.existsByJobAndUser(job, user)) {
            model.addAttribute("error",
                "You have already applied for this job!");
            model.addAttribute("job", job);
            return "apply-status";
        }

        // Get match score
        List<MatchingService.JobMatchResult> matches =
            matchingService.matchResumeWithJobs(resume);

        double score = matches.stream()
            .filter(m -> m.job.getId() == jobId)
            .mapToDouble(m -> m.matchScore)
            .findFirst().orElse(0.0);

        // Save application
        JobApplication application =
            new JobApplication(job, user, resume, score);
        jobApplicationRepository.save(application);

        model.addAttribute("success",
            "Applied successfully for " + job.getJobTitle() + "!");
        model.addAttribute("job", job);
        model.addAttribute("score", score);

        return "apply-status";
    }

    // View my applications
    @GetMapping("/my-applications")
    public String myApplications(Authentication authentication,
                                  Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        List<JobApplication> applications =
            jobApplicationRepository.findByUser(user);

        model.addAttribute("applications", applications);
        return "my-applications";
    }
}