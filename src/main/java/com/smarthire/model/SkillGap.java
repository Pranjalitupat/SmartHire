package com.smarthire.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_gap")
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "missing_skill")
    private String missingSkill;

    @Column(name = "learning_resource")
    private String learningResource;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    // Constructors
    public SkillGap() {}

    public SkillGap(Resume resume, Job job, String missingSkill,
                    String learningResource, Priority priority) {
        this.resume = resume;
        this.job = job;
        this.missingSkill = missingSkill;
        this.learningResource = learningResource;
        this.priority = priority;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getMissingSkill() { return missingSkill; }
    public void setMissingSkill(String missingSkill) { this.missingSkill = missingSkill; }

    public String getLearningResource() { return learningResource; }
    public void setLearningResource(String learningResource) { this.learningResource = learningResource; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}