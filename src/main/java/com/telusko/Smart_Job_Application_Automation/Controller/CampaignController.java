package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Model.*;
import com.telusko.Smart_Job_Application_Automation.Service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class CampaignController {

    private final CampaignService campaignService;
    private final RecruiterService recruiterService;
    private final EmailService emailService;
    private final CampaignRecipientService campaignRecipientService;
    private final   EmailTemplateService emailTemplateService;

    public CampaignController(CampaignService campaignService, RecruiterService recruiterService, EmailService emailService, CampaignRecipientService campaignRecipientService, EmailTemplateService emailTemplateService) {
        this.campaignService = campaignService;
        this.recruiterService = recruiterService;
        this.emailService = emailService;
        this.campaignRecipientService = campaignRecipientService;
        this.emailTemplateService = emailTemplateService;
    }


    @GetMapping("/campaigns")
    public String campaigns(
            @RequestParam(name = "sent", required = false) Integer sent, @RequestParam(name = "failed", required = false)
            Integer failed,
            @RequestParam(name = "error", required = false) String error,
            Model model) {
        model.addAttribute("campaigns", campaignService.getAllCampaigns());
        model.addAttribute("templates", emailTemplateService.getActiveTemplates());
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("recruiters", recruiterService.getAllRecruiters());
        if (sent != null) {
            model.addAttribute("success", sent + " emails sent successfully. " + (failed != null ? failed : 0) + " failed.");
        }
        if (error != null) {
            model.addAttribute("error",error);
        }
        return "campaigns";
    }


    @PostMapping("/campaigns/create")
    public String createCampaign(
            Campaign campaign,
            @RequestParam("templateId") Long templateId) {
        EmailTemplateEntity template =
                emailTemplateService.getTemplateById(templateId);
        campaign.setTemplate(template);
        campaignService.createCampaign(campaign);
        return "redirect:/campaigns";
    }


    @PostMapping("/campaigns/send")
    public String sendCampaign(
            @RequestParam("campaignId") Long campaignId,
            @RequestParam(name = "selectedIds", required = false) List<Long> selectedIds
    ) {

        if (selectedIds == null || selectedIds.isEmpty()) {
            return "redirect:/campaigns?error=Please select at least one recruiter.";
        }

        Campaign campaign =
                campaignService.getCampaignById(campaignId);

        List<Recruiter> recruiters =
                recruiterService.getRecruitersByIds(selectedIds);

        List<CampaignRecipient> recipients =
                campaignRecipientService.createRecipients(
                        campaign,
                        recruiters
                );

        int sentCount =
                emailService.sendCampaignEmailWithTracking(
                        recipients,
                        campaign.getTemplate(),
                        campaign.getPosition()
                );

        int failedCount =
                recipients.size() - sentCount;


        int totalUniqueRecipients =
                campaignRecipientService
                        .getRecipientsByCampaign(campaignId)
                        .size();

        campaign.setRecruiterCount(
                totalUniqueRecipients
        );


        if (recipients.isEmpty()) {

            campaign.setStatus("COMPLETED");

        } else if (sentCount == recipients.size()) {

            campaign.setStatus("COMPLETED");

        } else if (sentCount > 0) {

            campaign.setStatus("PARTIAL");

        } else {

            campaign.setStatus("FAILED");
        }

        campaignService.updateCampaign(
                campaign
        );
        return "redirect:/campaigns?sent="
                + sentCount
                + "&failed="
                + failedCount;
    }


    @GetMapping("/campaigns/{id}")
    public String campaignDetails(
            @PathVariable Long id,
            Model model
    ) {

        Campaign campaign =
                campaignService.getCampaignById(id);

        List<CampaignRecipient> recipients =
                campaignRecipientService
                        .getRecipientsByCampaign(id);


        long totalRecipients =
                recipients.size();


        long sentCount =
                recipients.stream()
                        .filter(recipient ->
                                "SENT".equalsIgnoreCase(
                                        recipient.getStatus()
                                )
                        )
                        .count();


        long failedCount =
                recipients.stream()
                        .filter(recipient ->
                                "FAILED".equalsIgnoreCase(
                                        recipient.getStatus()
                                )
                        )
                        .count();


        long pendingCount =
                recipients.stream()
                        .filter(recipient ->
                                "PENDING".equalsIgnoreCase(
                                        recipient.getStatus()
                                )
                        )
                        .count();


        model.addAttribute(
                "campaign",
                campaign
        );

        model.addAttribute(
                "recipients",
                recipients
        );

        model.addAttribute(
                "totalRecipients",
                totalRecipients
        );

        model.addAttribute(
                "sentCount",
                sentCount
        );

        model.addAttribute(
                "failedCount",
                failedCount
        );

        model.addAttribute(
                "pendingCount",
                pendingCount
        );


        return "campaign-details";
    }


    @PostMapping("/campaigns/{id}/retry-failed")
    public String retryFailedRecipients(
            @PathVariable Long id
    ) {

        Campaign campaign =
                campaignService.getCampaignById(id);

        List<CampaignRecipient> failedRecipients =
                campaignRecipientService
                        .getFailedRecipients(id);

        if (failedRecipients.isEmpty()) {

            return "redirect:/campaigns/"
                    + id
                    + "?message=No failed recipients to retry";
        }

        int sentCount =
                emailService
                        .sendCampaignEmailWithTracking(
                                failedRecipients,
                                campaign.getTemplate(),
                                campaign.getPosition()
                        );

        int failedCount =
                failedRecipients.size()
                        - sentCount;


        List<CampaignRecipient> allRecipients =
                campaignRecipientService
                        .getRecipientsByCampaign(id);

        long remainingFailed =
                allRecipients.stream()
                        .filter(r ->
                                "FAILED".equals(
                                        r.getStatus()
                                )
                        )
                        .count();


        if (remainingFailed == 0) {

            campaign.setStatus(
                    "COMPLETED"
            );

        } else {

            campaign.setStatus(
                    "PARTIAL");
        }
        campaignService.updateCampaign(
                campaign);
        return "redirect:/campaigns/"
                + id
                + "?sent="
                + sentCount
                + "&failed="
                + failedCount;
    }
}