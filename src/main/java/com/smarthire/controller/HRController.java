package com.smarthire.controller;

import com.smarthire.model.*;
import com.smarthire.model.JobApplication.Status;
import com.smarthire.repository.*;
import com.smarthire.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/hr")
public class HRController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private MatchingService matchingService;

    // ── HR Dashboard ──
    @GetMapping("/dashboard")
    public String hrDashboard(Authentication authentication,
                               Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Only HR can access
        if (user.getRole() != User.Role.HR) {
            return "redirect:/dashboard";
        }

        List<Job> myJobs = jobRepository.findByIsActiveTrue();
        int totalJobs = myJobs.size();

        // Count total applications
        int totalApplications = 0;
        for (Job job : myJobs) {
            totalApplications += jobApplicationRepository.countByJob(job);
        }

        model.addAttribute("user", user);
        model.addAttribute("myJobs", myJobs);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);

        return "hr/dashboard";
    }

    // ── Post New Job Page ──
    @GetMapping("/post-job")
    public String postJobPage(Authentication authentication,
                               Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        if (user.getRole() != User.Role.HR) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);
        return "hr/post-job";
    }

    // ── Handle Job Post ──
    @PostMapping("/post-job")
    public String postJob(@RequestParam String jobTitle,
                          @RequestParam String jobDescription,
                          @RequestParam String requiredSkills,
                          @RequestParam String experienceLevel,
                          @RequestParam(required = false) String location,
                          Authentication authentication,
                          Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        Job job = new Job();
        job.setJobTitle(jobTitle);
        job.setJobDescription(jobDescription);
        job.setRequiredSkills(requiredSkills);
        job.setExperienceLevel(
            Job.ExperienceLevel.valueOf(experienceLevel));
        job.setLocation(location);
        job.setActive(true);
        job.setCreatedAt(LocalDateTime.now());

        jobRepository.save(job);

        model.addAttribute("success",
            "Job posted successfully!");
        model.addAttribute("user", user);
        return "hr/post-job";
    }

    // ── View Candidates for a Job ──
    @GetMapping("/candidates/{jobId}")
    public String viewCandidates(@PathVariable int jobId,
                                  Authentication authentication,
                                  Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        if (user.getRole() != User.Role.HR) {
            return "redirect:/dashboard";
        }

        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return "redirect:/hr/dashboard";

        // Get all applications ranked by score
        List<JobApplication> applications =
            jobApplicationRepository
                .findByJobOrderByMatchScoreDesc(job);

        model.addAttribute("user", user);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        return "hr/candidates";
    }

    // ── Update Application Status ──
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam int applicationId,
                                @RequestParam String status,
                                Authentication authentication) {

        JobApplication application =
            jobApplicationRepository.findById(applicationId)
                .orElse(null);

        if (application != null) {
            application.setStatus(Status.valueOf(status));
            jobApplicationRepository.save(application);
            return "redirect:/hr/candidates/"
                + application.getJob().getId()
                + "?updated=true";
        }
        return "redirect:/hr/dashboard";
    }

    // ── Delete Job ──
    @PostMapping("/delete-job/{jobId}")
    public String deleteJob(@PathVariable int jobId,
                             Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        if (user.getRole() == User.Role.HR) {
            Job job = jobRepository.findById(jobId).orElse(null);
            if (job != null) {
                job.setActive(false);
                jobRepository.save(job);
            }
        }
        return "redirect:/hr/dashboard";
    }
}