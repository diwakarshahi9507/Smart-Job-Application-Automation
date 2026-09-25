package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.CampaignRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignRecipientRepository
        extends JpaRepository<CampaignRecipient, Long> {

    List<CampaignRecipient> findByCampaignId(
            Long campaignId
    );

    Optional<CampaignRecipient>
    findByCampaignIdAndRecruiterId(
            Long campaignId,
            Long recruiterId
    );

    List<CampaignRecipient> findByCampaignIdAndStatus(
            Long campaignId,
            String status
    );

    long countByStatus(String status);
}