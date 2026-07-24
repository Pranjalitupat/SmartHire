package com.smarthire.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.smarthire.model.Resume;
import com.smarthire.model.User;
import com.smarthire.service.MatchingService.JobMatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private MatchingService matchingService;

    public byte[] generateReport(User user, Resume resume) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        // ── Fonts ──
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22,
                Font.BOLD, new BaseColor(33, 97, 140));
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 13,
                Font.BOLD, new BaseColor(23, 32, 42));
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.NORMAL, BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 9,
                Font.NORMAL, new BaseColor(100, 100, 100));
        Font greenFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.BOLD, new BaseColor(39, 174, 96));
        Font redFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.BOLD, new BaseColor(192, 57, 43));
        Font whiteFont = new Font(Font.FontFamily.HELVETICA, 13,
                Font.BOLD, BaseColor.WHITE);

        // ── Header Banner ──
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell(
                new Phrase("🎯  SmartHire — Career Analysis Report", whiteFont));
        headerCell.setBackgroundColor(new BaseColor(33, 97, 140));
        headerCell.setPadding(15);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(headerCell);
        document.add(header);
        document.add(Chunk.NEWLINE);

        // ── Candidate Info ──
        addSectionTitle(document, "👤  Candidate Information", headingFont);
        addInfoRow(document, "Name", user.getFullName(), normalFont);
        addInfoRow(document, "Email", user.getEmail(), normalFont);
        addInfoRow(document, "Role", user.getRole().name(), normalFont);
        if (resume.getCandidatePhone() != null
                && !resume.getCandidatePhone().isEmpty()) {
            addInfoRow(document, "Phone",
                    resume.getCandidatePhone(), normalFont);
        }
        addInfoRow(document, "Report Date",
                java.time.LocalDate.now().toString(), normalFont);
        document.add(Chunk.NEWLINE);

        // ── Skills Found ──
        addSectionTitle(document, "💡  Skills Identified", headingFont);
        if (resume.getExtractedSkills() != null
                && !resume.getExtractedSkills().isEmpty()) {
            String[] skills = resume.getExtractedSkills().split(",\\s*");
            PdfPTable skillTable = new PdfPTable(4);
            skillTable.setWidthPercentage(100);
            for (String skill : skills) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(skill.trim(), greenFont));
                cell.setBackgroundColor(new BaseColor(214, 234, 248));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderColor(new BaseColor(174, 214, 241));
                skillTable.addCell(cell);
            }
            // Fill remaining cells
            int remainder = skills.length % 4;
            if (remainder != 0) {
                for (int i = 0; i < 4 - remainder; i++) {
                    PdfPCell empty = new PdfPCell(new Phrase(""));
                    empty.setBorder(Rectangle.NO_BORDER);
                    skillTable.addCell(empty);
                }
            }
            document.add(skillTable);
        }
        document.add(Chunk.NEWLINE);

        // ── Job Matches ──
        addSectionTitle(document, "💼  Top Job Matches", headingFont);
        List<JobMatchResult> matches =
                matchingService.matchResumeWithJobs(resume);

        if (!matches.isEmpty()) {
            PdfPTable matchTable = new PdfPTable(4);
            matchTable.setWidthPercentage(100);
            matchTable.setWidths(new float[]{3, 2, 2, 2});

            // Table header
            String[] headers = {"Job Title", "Match Score",
                    "Match Level", "Experience"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, whiteFont));
                cell.setBackgroundColor(new BaseColor(33, 97, 140));
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                matchTable.addCell(cell);
            }

            // Table rows
            boolean alternate = false;
            for (JobMatchResult match : matches) {
                BaseColor rowColor = alternate
                        ? new BaseColor(235, 245, 251)
                        : BaseColor.WHITE;

                PdfPCell titleCell = new PdfPCell(
                        new Phrase(match.job.getJobTitle(), normalFont));
                titleCell.setBackgroundColor(rowColor);
                titleCell.setPadding(7);
                matchTable.addCell(titleCell);

                Font scoreFont = new Font(Font.FontFamily.HELVETICA,
                        11, Font.BOLD,
                        match.matchScore >= 80
                                ? new BaseColor(39, 174, 96)
                                : match.matchScore >= 60
                                ? new BaseColor(243, 156, 18)
                                : new BaseColor(192, 57, 43));
                PdfPCell scoreCell = new PdfPCell(
                        new Phrase(match.matchScore + "%", scoreFont));
                scoreCell.setBackgroundColor(rowColor);
                scoreCell.setPadding(7);
                scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                matchTable.addCell(scoreCell);

                PdfPCell labelCell = new PdfPCell(
                        new Phrase(match.matchLabel, normalFont));
                labelCell.setBackgroundColor(rowColor);
                labelCell.setPadding(7);
                labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                matchTable.addCell(labelCell);

                PdfPCell expCell = new PdfPCell(new Phrase(
                        match.job.getExperienceLevel().name(), normalFont));
                expCell.setBackgroundColor(rowColor);
                expCell.setPadding(7);
                expCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                matchTable.addCell(expCell);

                alternate = !alternate;
            }
            document.add(matchTable);
        }
        document.add(Chunk.NEWLINE);

        // ── Skill Gap for Top Match ──
        if (!matches.isEmpty()) {
            JobMatchResult top = matches.get(0);
            addSectionTitle(document,
                    "📊  Skill Gap — " + top.job.getJobTitle(),
                    headingFont);

            // Matched
            if (!top.matchedSkills.isEmpty()) {
                document.add(new Paragraph("✅  Skills You Have:",
                        headingFont));
                document.add(Chunk.NEWLINE);
                PdfPTable t = new PdfPTable(4);
                t.setWidthPercentage(100);
                for (String s : top.matchedSkills) {
                    PdfPCell c = new PdfPCell(
                            new Phrase(s, greenFont));
                    c.setBackgroundColor(new BaseColor(213, 245, 227));
                    c.setPadding(6);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    t.addCell(c);
                }
                fillEmptyCells(t, top.matchedSkills.size(), 4);
                document.add(t);
                document.add(Chunk.NEWLINE);
            }

            // Missing
            if (!top.missingSkills.isEmpty()) {
                document.add(new Paragraph("❌  Skills to Learn:",
                        headingFont));
                document.add(Chunk.NEWLINE);
                PdfPTable t = new PdfPTable(4);
                t.setWidthPercentage(100);
                for (String s : top.missingSkills) {
                    PdfPCell c = new PdfPCell(
                            new Phrase(s, redFont));
                    c.setBackgroundColor(new BaseColor(245, 213, 213));
                    c.setPadding(6);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    t.addCell(c);
                }
                fillEmptyCells(t, top.missingSkills.size(), 4);
                document.add(t);
                document.add(Chunk.NEWLINE);
            }
        }

        // ── Footer ──
        document.add(Chunk.NEWLINE);
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        PdfPCell footerCell = new PdfPCell(new Phrase(
                "Generated by SmartHire  |  " + java.time.LocalDate.now()
                        + "  |  Your Smart Career Companion",
                smallFont));
        footerCell.setBackgroundColor(new BaseColor(235, 237, 239));
        footerCell.setPadding(10);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footerCell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(footerCell);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    // Helper — section title
    private void addSectionTitle(Document doc, String title, Font font)
            throws Exception {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Phrase(title, font));
        c.setBackgroundColor(new BaseColor(214, 234, 248));
        c.setPadding(8);
        c.setBorderColor(new BaseColor(174, 214, 241));
        t.addCell(c);
        doc.add(t);
        doc.add(Chunk.NEWLINE);
    }

    // Helper — info row
    private void addInfoRow(Document doc, String label,
                             String value, Font font) throws Exception {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{2, 5});

        Font boldFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.BOLD, BaseColor.BLACK);
        PdfPCell lc = new PdfPCell(new Phrase(label, boldFont));
        lc.setPadding(6);
        lc.setBackgroundColor(new BaseColor(245, 245, 245));
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value, font));
        vc.setPadding(6);
        t.addCell(vc);
        doc.add(t);
    }

    // Helper — fill empty cells in table
    private void fillEmptyCells(PdfPTable table,
                                  int count, int cols) {
        int remainder = count % cols;
        if (remainder != 0) {
            for (int i = 0; i < cols - remainder; i++) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                table.addCell(empty);
            }
        }
    }
}