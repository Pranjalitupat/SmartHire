package com.smarthire.controller;

import com.smarthire.model.Resume;
import com.smarthire.model.User;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;

@Controller
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeParserService resumeParserService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Show upload page
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    // Handle resume upload
    @PostMapping("/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file,
                               Authentication authentication,
                               Model model) {
        try {
            // Get logged in user
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).get();

            // Save file to uploads folder
            String fileName = System.currentTimeMillis() 
                              + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath,
                      StandardCopyOption.REPLACE_EXISTING);

            // Extract text from resume
            String extractedText = resumeParserService.extractText(file);

            // Extract information
            String skills = resumeParserService.extractSkills(extractedText);
            String email2 = resumeParserService.extractEmail(extractedText);
            String phone = resumeParserService.extractPhone(extractedText);

            // Save resume to database
            Resume resume = new Resume();
            resume.setUser(user);
            resume.setFileName(file.getOriginalFilename());
            resume.setFilePath(filePath.toString());
            resume.setExtractedText(extractedText);
            resume.setExtractedSkills(skills);
            resume.setCandidateEmail(email2);
            resume.setCandidatePhone(phone);
            resume.setUploadedAt(LocalDateTime.now());

            resumeRepository.save(resume);

            System.out.println("✅ Resume saved! Skills: " + skills);

            model.addAttribute("success", 
                "Resume uploaded successfully!");
            model.addAttribute("skills", skills);
            model.addAttribute("phone", phone);
            model.addAttribute("emailFound", email2);

            return "upload";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", 
                "Error uploading resume: " + e.getMessage());
            return "upload";
        }
    }
}