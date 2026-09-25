package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.AppSettings;
import com.telusko.Smart_Job_Application_Automation.Model.CampaignRecipient;
import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateService templateService;
    private final RecruiterService recruiterService;
    private final CampaignRecipientService campaignRecipientService;
    private final SettingsService settingsService;


    public EmailService(
            JavaMailSender mailSender,
            TemplateService templateService,
            RecruiterService recruiterService,
            CampaignRecipientService campaignRecipientService,
            SettingsService settingsService
    ) {
        this.mailSender = mailSender;
        this.templateService = templateService;
        this.recruiterService = recruiterService;
        this.campaignRecipientService = campaignRecipientService;
        this.settingsService = settingsService;
    }

    @Value("${app.resume.path:}")
    private String resumePath;

    public int sendEmailWithDbTemplate(
            List<Recruiter> recruiters,
            EmailTemplateEntity template
    ) {

        int sentCount = 0;

        for (Recruiter recruiter : recruiters) {

            try {

                // Basic email validation
                if (recruiter.getEmail() == null
                        || recruiter.getEmail().isBlank()) {

                    markRecruiterFailed(recruiter);
                    continue;
                }


                MimeMessage message =
                        mailSender.createMimeMessage();

                MimeMessageHelper helper =
                        new MimeMessageHelper(
                                message,
                                true,
                                "UTF-8"
                        );


                helper.setTo(
                        recruiter.getEmail()
                );

                AppSettings settings = settingsService.getSettings();

                String senderEmail = settings.getSenderEmail();
                String senderName = settings.getSenderName();

                if (senderEmail != null && !senderEmail.isBlank()) {

                    if (senderName != null && !senderName.isBlank()) {
                        helper.setFrom(senderEmail, senderName);
                    } else {
                        helper.setFrom(senderEmail);
                    }
                }

                String effectivePosition =
                        getEffectivePosition(
                                recruiter,
                                recruiter.getPosition()
                        );

                String subject =
                        templateService.prepareSubject(
                                template,
                                recruiter,
                                effectivePosition
                        );

                String body =
                        templateService.prepareTemplate(
                                template,
                                recruiter,
                                effectivePosition
                        );

                body = appendEmailSignature(body);

                helper.setSubject(subject);

                helper.setText(
                        body,
                        true
                );


                // Optional resume attachment
                attachResumeIfAvailable(helper);


                // Send email
                mailSender.send(message);


                // Update recruiter status
                recruiter.setStatus("SENT");
                recruiter.setSentTime(
                        LocalDateTime.now()
                );

                recruiterService.saveRecruiter(
                        recruiter
                );

                sentCount++;

            } catch (Exception e) {

                markRecruiterFailed(
                        recruiter
                );

                System.out.println(
                        "Email failed for "
                                + recruiter.getEmail()
                                + " : "
                                + e.getMessage()
                );
            }
        }

        return sentCount;
    }



    public int sendCampaignEmailWithTracking(
            List<CampaignRecipient> recipients,
            EmailTemplateEntity campaignTemplate,
            String campaignPosition
    ) {

        int sentCount = 0;

        for (CampaignRecipient recipient : recipients) {

            Recruiter recruiter =
                    recipient.getRecruiter();

            try {

                // Store time of current attempt
                LocalDateTime attemptTime =
                        LocalDateTime.now();

                recipient.setAttemptTime(
                        attemptTime
                );


                // Basic email validation
                if (recruiter == null
                        || recruiter.getEmail() == null
                        || recruiter.getEmail().isBlank()) {

                    markCampaignRecipientFailed(
                            recipient,
                            recruiter,
                            new RuntimeException(
                                    "Recruiter email is empty"
                            )
                    );

                    continue;
                }


                // Validate campaign template
                if (campaignTemplate == null) {

                    markCampaignRecipientFailed(
                            recipient,
                            recruiter,
                            new RuntimeException(
                                    "Campaign email template is not selected"
                            )
                    );

                    continue;
                }


                MimeMessage message =
                        mailSender.createMimeMessage();

                MimeMessageHelper helper =
                        new MimeMessageHelper(
                                message,
                                true,
                                "UTF-8"
                        );


                helper.setTo(
                        recruiter.getEmail()
                );

                AppSettings settings = settingsService.getSettings();

                String senderEmail = settings.getSenderEmail();
                String senderName = settings.getSenderName();

                if (senderEmail != null && !senderEmail.isBlank()) {

                    if (senderName != null && !senderName.isBlank()) {
                        helper.setFrom(senderEmail, senderName);
                    } else {
                        helper.setFrom(senderEmail);
                    }
                }

                String effectivePosition =
                        getEffectivePosition(
                                recruiter,
                                campaignPosition
                        );

                String subject =
                        templateService.prepareSubject(
                                campaignTemplate,
                                recruiter,
                                effectivePosition
                        );

                String body =
                        templateService.prepareTemplate(
                                campaignTemplate,
                                recruiter,
                                effectivePosition
                        );

                body = appendEmailSignature(body);

                helper.setSubject(subject);

                helper.setText(
                        body,
                        true
                );


                // Optional resume attachment
                attachResumeIfAvailable(helper);


                // Actual email send
                mailSender.send(message);


                LocalDateTime now =
                        LocalDateTime.now();


                // -------------------------
                // CampaignRecipient SUCCESS
                // -------------------------
                recipient.setStatus("SENT");

                recipient.setSentTime(
                        now
                );

                recipient.setAttemptTime(
                        now
                );

                // Remove previous failure reason after successful retry
                recipient.setErrorMessage(
                        null
                );

                campaignRecipientService.saveRecipient(
                        recipient
                );


                // -------------------------
                // Recruiter latest status
                // -------------------------
                recruiter.setStatus("SENT");

                recruiter.setSentTime(
                        now
                );

                recruiterService.saveRecruiter(
                        recruiter
                );


                sentCount++;

            } catch (Exception e) {

                markCampaignRecipientFailed(
                        recipient,
                        recruiter,
                        e
                );

                String recruiterEmail =
                        recruiter != null
                                ? recruiter.getEmail()
                                : "UNKNOWN";

                System.out.println(
                        "Campaign email failed for "
                                + recruiterEmail
                                + " : "
                                + e.getMessage()
                );
            }
        }

        return sentCount;
    }


    // =========================================================
    // 3. DIRECT RECRUITER FAILURE STATUS
    // =========================================================
    private void markRecruiterFailed(
            Recruiter recruiter
    ) {

        if (recruiter == null) {
            return;
        }

        recruiter.setStatus(
                "FAILED"
        );

        recruiter.setSentTime(
                null
        );

        recruiterService.saveRecruiter(
                recruiter
        );
    }



    private void markCampaignRecipientFailed(
            CampaignRecipient recipient,
            Recruiter recruiter,
            Exception e
    ) {

        LocalDateTime now =
                LocalDateTime.now();


        // Update campaign recipient
        if (recipient != null) {

            recipient.setStatus(
                    "FAILED"
            );

            recipient.setSentTime(
                    null
            );

            recipient.setAttemptTime(
                    now
            );

            String errorMessage =
                    e != null
                            && e.getMessage() != null
                            && !e.getMessage().isBlank()
                            ? e.getMessage()
                            : "Unknown email sending error";

            recipient.setErrorMessage(
                    errorMessage
            );

            campaignRecipientService.saveRecipient(
                    recipient
            );
        }


        // Update recruiter latest status
        if (recruiter != null) {

            recruiter.setStatus(
                    "FAILED"
            );

            recruiter.setSentTime(
                    null
            );

            recruiterService.saveRecruiter(
                    recruiter
            );
        }
    }



    private void attachResumeIfAvailable(
            MimeMessageHelper helper
    ) throws Exception {

        AppSettings settings =
                settingsService.getSettings();

        String resumePath =
                settings.getResumePath();

        if (resumePath == null
                || resumePath.isBlank()) {
            return;
        }

        File resumeFile =
                new File(resumePath);

        if (!resumeFile.exists()
                || !resumeFile.isFile()) {

            System.out.println(
                    "Resume file not found: "
                            + resumePath
            );

            return;
        }

        FileSystemResource resource =
                new FileSystemResource(
                        resumeFile
                );

        helper.addAttachment(
                resumeFile.getName(),
                resource
        );
    }

    private String getEffectivePosition(
            Recruiter recruiter,
            String requestedPosition
    ) {

        if (requestedPosition != null
                && !requestedPosition.isBlank()) {
            return requestedPosition;
        }

        if (recruiter != null
                && recruiter.getPosition() != null
                && !recruiter.getPosition().isBlank()) {
            return recruiter.getPosition();
        }

        AppSettings settings =
                settingsService.getSettings();

        if (settings.getDefaultPosition() != null
                && !settings.getDefaultPosition().isBlank()) {
            return settings.getDefaultPosition();
        }

        return "";
    }

    private String appendEmailSignature(
            String body
    ) {

        AppSettings settings =
                settingsService.getSettings();

        String signature =
                settings.getEmailSignature();

        if (signature == null
                || signature.isBlank()) {
            return body;
        }

        String formattedSignature =
                signature.replace(
                        "\n",
                        "<br>"
                );

        return body
                + "<br><br>"
                + formattedSignature;
    }
}
