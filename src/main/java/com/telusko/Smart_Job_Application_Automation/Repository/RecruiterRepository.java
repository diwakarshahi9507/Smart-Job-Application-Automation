package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {

    Optional<Recruiter> findByEmail(String email);

    boolean existsByEmail(String email);
    long countByStatus(String status);

    List<Recruiter> findTop5ByStatusInOrderByIdDesc(List<String> statuses);

    List<Recruiter> findByActiveTrue();

    long countByActiveTrue();

    long countByStatusAndActiveTrue(String status);

}