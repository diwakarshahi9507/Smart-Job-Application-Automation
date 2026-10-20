package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.EmailActivity;
import com.telusko.Smart_Job_Application_Automation.Repository.EmailActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailActivityService {

    private final EmailActivityRepository emailActivityRepository;

    public EmailActivityService(EmailActivityRepository emailActivityRepository) {
        this.emailActivityRepository = emailActivityRepository;
    }

    // Save a new email activity
    public EmailActivity saveActivity(EmailActivity activity) {
        return emailActivityRepository.save(activity);
    }

    // Get complete email history - latest first
    public List<EmailActivity> getAllActivities() {
        return emailActivityRepository.findAllByOrderByAttemptTimeDesc();
    }

    // Get email history of a particular recruiter
    public List<EmailActivity> getActivitiesByRecruiter(Long recruiterId) {
        return emailActivityRepository.findByRecruiterIdOrderByAttemptTimeDesc(recruiterId);
    }
}
