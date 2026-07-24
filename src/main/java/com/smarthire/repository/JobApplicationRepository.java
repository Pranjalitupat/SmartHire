package com.smarthire.repository;

import com.smarthire.model.Job;
import com.smarthire.model.JobApplication;
import com.smarthire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository 
    extends JpaRepository<JobApplication, Integer> {

    List<JobApplication> findByJobOrderByMatchScoreDesc(Job job);
    List<JobApplication> findByUser(User user);
    Optional<JobApplication> findByJobAndUser(Job job, User user);
    boolean existsByJobAndUser(Job job, User user);
    int countByJob(Job job);
}