package com.smarthire.controller;

import com.smarthire.model.Resume;
import com.smarthire.model.User;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               Model model) {

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already registered! Please login.");
            return "register";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role));
        user.setCreatedAt(java.time.LocalDateTime.now());

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();

        // Redirect HR to HR dashboard
        if (user.getRole() == User.Role.HR) {
            return "redirect:/hr/dashboard";
        }

        Resume resume = resumeRepository
            .findTopByUserOrderByUploadedAtDesc(user)
            .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("resume", resume);

        if (resume != null) {
            String skills = resume.getExtractedSkills();
            int skillCount = 0;
            if (skills != null && !skills.isEmpty()) {
                skillCount = skills.split(",").length;
            }
            model.addAttribute("skillCount", skillCount);

            List<MatchingService.JobMatchResult> matches =
                matchingService.matchResumeWithJobs(resume);

            if (!matches.isEmpty()) {
                model.addAttribute("topMatch", matches.get(0));
                model.addAttribute("totalMatches", matches.size());
            }
        }

        return "dashboard";
    }
}