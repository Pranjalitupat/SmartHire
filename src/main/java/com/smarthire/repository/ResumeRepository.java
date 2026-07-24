package com.smarthire.repository;

import com.smarthire.model.Resume;
import com.smarthire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {

    List<Resume> findByUser(User user);
    Optional<Resume> findTopByUserOrderByUploadedAtDesc(User user);
}