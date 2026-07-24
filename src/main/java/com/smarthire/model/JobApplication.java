package com.smarthire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "match_score")
    private double matchScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.APPLIED;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    public enum Status {
        APPLIED, SHORTLISTED, REJECTED, SELECTED
    }

    // Constructors
    public JobApplication() {}

    public JobApplication(Job job, User user, Resume resume,
                          double matchScore) {
        this.job = job;
        this.user = user;
        this.resume = resume;
        this.matchScore = matchScore;
        this.status = Status.APPLIED;
        this.appliedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}