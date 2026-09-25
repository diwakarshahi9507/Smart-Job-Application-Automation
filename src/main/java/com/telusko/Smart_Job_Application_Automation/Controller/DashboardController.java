package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Service.CampaignService;
import com.telusko.Smart_Job_Application_Automation.Service.RecruiterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final RecruiterService recruiterService;
    private final CampaignService campaignService;

    public DashboardController(
            RecruiterService recruiterService,
            CampaignService campaignService
    ) {
        this.recruiterService = recruiterService;
        this.campaignService = campaignService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalRecruiters",
                recruiterService.getTotalRecruiters()
        );

        model.addAttribute(
                "pendingCount",
                recruiterService.getPendingCount()
        );

        model.addAttribute(
                "sentCount",
                recruiterService.getSentCount()
        );

        model.addAttribute(
                "failedCount",
                recruiterService.getFailedCount()
        );

        model.addAttribute(
                "recentActivity",
                recruiterService.getRecentEmailActivity()
        );

        model.addAttribute(
                "recentCampaigns",
                campaignService.getRecentCampaigns()
        );

        return "dashboard";
    }
}