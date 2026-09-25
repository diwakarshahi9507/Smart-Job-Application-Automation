package com.telusko.Smart_Job_Application_Automation.Service;
import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    public String prepareTemplate(
            EmailTemplateEntity template,
            Recruiter recruiter,
            String campaignPosition
    ) {
        String body =
                template.getBody();

        String firstName =
                recruiter.getFirstName() != null
                        ? recruiter.getFirstName()
                        : "";
        String company =
                recruiter.getCompany() != null
                        ? recruiter.getCompany()
                        : "";
        String position =
                campaignPosition != null
                        && !campaignPosition.isBlank()
                        ? campaignPosition
                        : recruiter.getPosition();
        if (position == null) {
            position = "";
        }
        body = body
                .replace(
                        "{{firstName}}",
                        firstName
                )
                .replace(
                        "{{company}}",
                        company
                )
                .replace(
                        "{{position}}",
                        position
                );
        return body;
    }


    public String prepareSubject(
            EmailTemplateEntity template,
            Recruiter recruiter,
            String campaignPosition
    ) {
        String subject =
                template.getSubject();

        String firstName =
                recruiter.getFirstName() != null
                        ? recruiter.getFirstName()
                        : "";
        String company =
                recruiter.getCompany() != null
                        ? recruiter.getCompany()
                        : "";
        String position =
                campaignPosition != null
                        && !campaignPosition.isBlank()
                        ? campaignPosition
                        : recruiter.getPosition();

        if (position == null) {
            position = "";
        }
        subject = subject
                .replace(
                        "{{firstName}}",
                        firstName
                )
                .replace(
                        "{{company}}",
                        company
                )
                .replace(
                        "{{position}}",
                        position
                );
        return subject;
    }
}
