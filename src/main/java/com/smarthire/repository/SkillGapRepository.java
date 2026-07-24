package com.smarthire.repository;

import com.smarthire.model.Job;
import com.smarthire.model.Resume;
import com.smarthire.model.SkillGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillGapRepository extends JpaRepository<SkillGap, Integer> {

    List<SkillGap> findByResumeAndJob(Resume resume, Job job);
    void deleteByResumeAndJob(Resume resume, Job job);
}