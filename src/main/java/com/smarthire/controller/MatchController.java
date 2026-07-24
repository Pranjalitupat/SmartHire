package com.smarthire.controller;

import com.smarthire.model.Resume;
import com.smarthire.model.User;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.MatchingService;
import com.smarthire.service.MatchingService.JobMatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MatchController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/match")
    public String showMatches(Authentication authentication, Model model) {

        // Get logged in user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Get latest resume
        Resume resume = resumeRepository
            .findTopByUserOrderByUploadedAtDesc(user)
            .orElse(null);

        if (resume == null) {
            model.addAttribute("error",
                "Please upload your resume first!");
            return "match";
        }

        // Get job matches
        List<JobMatchResult> matches =
            matchingService.matchResumeWithJobs(resume);

        model.addAttribute("resume", resume);
        model.addAttribute("matches", matches);

        return "match";
    }
}