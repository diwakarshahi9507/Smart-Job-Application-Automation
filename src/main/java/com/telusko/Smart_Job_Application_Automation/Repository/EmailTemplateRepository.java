package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailTemplateRepository
        extends JpaRepository<EmailTemplateEntity, Long> {

    List<EmailTemplateEntity> findByActiveTrueOrderByUpdatedTimeDesc();

    Optional<EmailTemplateEntity> findByNameIgnoreCase(String name);

}