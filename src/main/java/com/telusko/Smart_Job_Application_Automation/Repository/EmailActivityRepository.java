package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.EmailActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailActivityRepository extends JpaRepository<EmailActivity, Long> {

    List<EmailActivity> findAllByOrderByAttemptTimeDesc();

    List<EmailActivity> findByRecruiterIdOrderByAttemptTimeDesc(Long recruiterId);
}