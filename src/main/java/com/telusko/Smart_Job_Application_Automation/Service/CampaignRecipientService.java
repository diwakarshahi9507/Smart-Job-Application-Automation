package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.Campaign;
import com.telusko.Smart_Job_Application_Automation.Model.CampaignRecipient;
import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import com.telusko.Smart_Job_Application_Automation.Repository.CampaignRecipientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignRecipientService {

    private final CampaignRecipientRepository campaignRecipientRepository;

    public CampaignRecipientService(
            CampaignRecipientRepository campaignRecipientRepository
    ) {
        this.campaignRecipientRepository = campaignRecipientRepository;
    }


    public List<CampaignRecipient> createRecipients(
            Campaign campaign,
            List<Recruiter> recruiters
    ) {
        List<CampaignRecipient> recipients =
                new ArrayList<>();
        for (Recruiter recruiter : recruiters) {
            var existingRecipient =
                    campaignRecipientRepository
                            .findByCampaignIdAndRecruiterId(
                                    campaign.getId(),
                                    recruiter.getId()
                            );

            if (existingRecipient.isPresent()) {
                CampaignRecipient recipient =
                        existingRecipient.get();
                if ("SENT".equalsIgnoreCase(
                        recipient.getStatus()
                )) {
                    System.out.println(
                            "Already sent in this campaign: "
                                    + recruiter.getEmail()
                    );
                    continue;
                }
                recipients.add(
                        recipient
                );

                continue;
            }


            CampaignRecipient recipient =
                    new CampaignRecipient();

            recipient.setCampaign(
                    campaign
            );

            recipient.setRecruiter(
                    recruiter
            );

            recipient.setStatus(
                    "PENDING"
            );

            recipient.setSentTime(
                    null
            );

            CampaignRecipient savedRecipient =
                    campaignRecipientRepository.save(
                            recipient
                    );
            recipients.add(
                    savedRecipient
            );
        }


        return recipients;
    }

    public CampaignRecipient saveRecipient(CampaignRecipient recipient) {
        return campaignRecipientRepository.save(
                recipient
        );
    }


    public List<CampaignRecipient> getRecipientsByCampaign(Long campaignId) {
        return campaignRecipientRepository
                .findByCampaignId(
                        campaignId
                );
    }


    public List<CampaignRecipient> getFailedRecipients(
            Long campaignId) {
        return campaignRecipientRepository
                .findByCampaignIdAndStatus(
                        campaignId,
                        "FAILED");
    }


    public long getTotalEmailAttempts() {
        return campaignRecipientRepository.count();
    }

    public long getTotalSentEmails() {
        return campaignRecipientRepository
                .countByStatus("SENT");
    }

    public long getTotalFailedEmails() {
        return campaignRecipientRepository
                .countByStatus("FAILED");
    }

    public long getTotalPendingEmails() {
        return campaignRecipientRepository
                .countByStatus("PENDING");
    }
}