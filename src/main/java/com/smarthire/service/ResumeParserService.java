package com.smarthire.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

@Service
public class ResumeParserService {

    // Skills master list
    private static final List<String> SKILLS_LIST = Arrays.asList(
        "java", "python", "c++", "javascript", "php", "html", "css",
        "spring boot", "hibernate", "react", "angular", "node.js",
        "mysql", "oracle", "mongodb", "postgresql", "redis",
        "docker", "git", "maven", "jenkins", "linux",
        "rest api", "json", "microservices", "jdbc", "servlets",
        "bootstrap", "jquery", "aws", "data structures", "oops"
    );

    // Extract text from PDF
    public String extractTextFromPDF(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    // Extract text from DOCX
    public String extractTextFromDOCX(MultipartFile file) throws IOException {
        XWPFDocument document = new XWPFDocument(file.getInputStream());
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            text.append(paragraph.getText()).append("\n");
        }
        document.close();
        return text.toString();
    }

    // Extract text based on file type
    public String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDOCX(file);
        }
        return "";
    }

    // Extract skills from text
    public String extractSkills(String text) {
        String lowerText = text.toLowerCase();
        List<String> foundSkills = new ArrayList<>();

        for (String skill : SKILLS_LIST) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill.toUpperCase());
            }
        }

        return String.join(", ", foundSkills);
    }

    // Extract email from text
    public String extractEmail(String text) {
        Pattern pattern = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // Extract phone from text
    public String extractPhone(String text) {
        Pattern pattern = Pattern.compile(
            "(\\+91|0)?[6-9][0-9]{9}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}