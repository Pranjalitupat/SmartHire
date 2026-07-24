package com.smarthire.controller;

import com.smarthire.model.Resume;
import com.smarthire.model.User;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).get();

            Resume resume = resumeRepository
                .findTopByUserOrderByUploadedAtDesc(user)
                .orElse(null);

            if (resume == null) {
                return ResponseEntity.badRequest().build();
            }

            byte[] pdf = reportService.generateReport(user, resume);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.attachment()
                    .filename("SmartHire_Report_"
                        + user.getFullName().replace(" ", "_")
                        + ".pdf")
                    .build()
            );

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}