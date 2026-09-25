package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import com.telusko.Smart_Job_Application_Automation.Repository.EmailTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateService(
            EmailTemplateRepository emailTemplateRepository
    ) {
        this.emailTemplateRepository =
                emailTemplateRepository;
    }


    public EmailTemplateEntity createTemplate(
            EmailTemplateEntity template
    ) {

        String templateName =
                template.getName().trim();


        var existingTemplate =
                emailTemplateRepository
                        .findByNameIgnoreCase(
                                templateName
                        );


        // If same template already exists
        if (existingTemplate.isPresent()) {

            EmailTemplateEntity existing =
                    existingTemplate.get();


            // If it was soft deleted, reactivate it
            if (!existing.isActive()) {

                existing.setActive(true);

                existing.setName(
                        templateName
                );

                existing.setSubject(
                        template.getSubject()
                );

                existing.setBody(
                        template.getBody()
                );

                existing.setUpdatedTime(
                        LocalDateTime.now()
                );

                return emailTemplateRepository.save(
                        existing
                );
            }


            // Already active = duplicate
            throw new RuntimeException(
                    "Template with this name already exists."
            );
        }


        // Completely new template
        template.setName(
                templateName
        );

        template.setActive(true);

        template.setCreatedTime(
                LocalDateTime.now()
        );

        template.setUpdatedTime(
                LocalDateTime.now()
        );

        return emailTemplateRepository.save(
                template
        );
    }


    public List<EmailTemplateEntity> getActiveTemplates() {

        return emailTemplateRepository
                .findByActiveTrueOrderByUpdatedTimeDesc();
    }


    public List<EmailTemplateEntity> getAllTemplates() {

        return emailTemplateRepository.findAll();
    }


    public EmailTemplateEntity getTemplateById(
            Long id
    ) {

        return emailTemplateRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Template not found with id: " + id
                        )
                );
    }


    public EmailTemplateEntity updateTemplate(
            EmailTemplateEntity template
    ) {

        EmailTemplateEntity existing =
                getTemplateById(
                        template.getId()
                );


        var duplicateTemplate =
                emailTemplateRepository
                        .findByNameIgnoreCase(
                                template.getName().trim()
                        );


        if (duplicateTemplate.isPresent()
                && !duplicateTemplate.get()
                .getId()
                .equals(existing.getId())) {

            throw new RuntimeException(
                    "Another template with this name already exists."
            );
        }


        existing.setName(
                template.getName().trim()
        );

        existing.setSubject(
                template.getSubject()
        );

        existing.setBody(
                template.getBody()
        );

        existing.setUpdatedTime(
                LocalDateTime.now()
        );


        return emailTemplateRepository.save(
                existing
        );
    }


    public void deleteTemplate(Long id) {

        EmailTemplateEntity template =
                emailTemplateRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Template not found with id: " + id
                                )
                        );

        template.setActive(false);
        template.setUpdatedTime(LocalDateTime.now());

        emailTemplateRepository.save(template);
    }
}