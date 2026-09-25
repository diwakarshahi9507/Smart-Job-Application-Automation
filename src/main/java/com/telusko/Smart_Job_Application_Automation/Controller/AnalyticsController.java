package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Service.CampaignRecipientService;
import com.telusko.Smart_Job_Application_Automation.Service.CampaignService;
import com.telusko.Smart_Job_Application_Automation.Service.RecruiterService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalyticsController {

    private final RecruiterService recruiterService;
    private final CampaignService campaignService;
    private final CampaignRecipientService campaignRecipientService;

    public AnalyticsController(
            RecruiterService recruiterService,
            CampaignService campaignService,
            CampaignRecipientService campaignRecipientService
    ) {
        this.recruiterService = recruiterService;
        this.campaignService = campaignService;
        this.campaignRecipientService = campaignRecipientService;
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {

        // Recruiter-level current status
        long totalRecruiters =
                recruiterService.countActiveRecruiters();

        long sentCount =
                recruiterService.countSentRecruiters();

        long failedCount =
                recruiterService.countFailedRecruiters();

        long pendingCount =
                recruiterService.countPendingRecruiters();

        double successRate = 0.0;

        if (totalRecruiters > 0) {
            successRate =
                    ((double) sentCount / totalRecruiters) * 100;
        }


        // Campaign-level historical statistics
        long totalEmailAttempts =
                campaignRecipientService.getTotalEmailAttempts();

        long totalCampaignSent =
                campaignRecipientService.getTotalSentEmails();

        long totalCampaignFailed =
                campaignRecipientService.getTotalFailedEmails();

        long totalCampaignPending =
                campaignRecipientService.getTotalPendingEmails();

        double campaignSuccessRate = 0.0;

        if (totalEmailAttempts > 0) {
            campaignSuccessRate =
                    ((double) totalCampaignSent / totalEmailAttempts) * 100;
        }


        long totalCampaigns =
                campaignService.countCampaigns();


        model.addAttribute(
                "totalRecruiters",
                totalRecruiters
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

        model.addAttribute(
                "successRate",
                successRate
        );


        model.addAttribute(
                "totalCampaigns",
                totalCampaigns
        );

        model.addAttribute(
                "totalEmailAttempts",
                totalEmailAttempts
        );

        model.addAttribute(
                "totalCampaignSent",
                totalCampaignSent
        );

        model.addAttribute(
                "totalCampaignFailed",
                totalCampaignFailed
        );

        model.addAttribute(
                "totalCampaignPending",
                totalCampaignPending
        );

        model.addAttribute(
                "campaignSuccessRate",
                campaignSuccessRate
        );


        return "analytics";
    }
}