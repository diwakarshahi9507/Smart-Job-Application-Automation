package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.Campaign;
import com.telusko.Smart_Job_Application_Automation.Repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }


    public Campaign createCampaign(Campaign campaign) {
        campaign.setCreatedTime(LocalDateTime.now());
        if (campaign.getStatus() == null || campaign.getStatus().isBlank()) {
            campaign.setStatus("CREATED");
        }
        if (campaign.getRecruiterCount() == null) {
            campaign.setRecruiterCount(0);
        }
        return campaignRepository.save(campaign);
    }


    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }


    public List<Campaign> getRecentCampaigns() {
        return campaignRepository.findTop5ByOrderByCreatedTimeDesc();
    }


    public Campaign getCampaignById(Long id) {
        return campaignRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Campaign not found with id: " + id)
                        );
    }


    public Campaign updateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }


    public long countCampaigns() {
        return campaignRepository.count();
    }
}