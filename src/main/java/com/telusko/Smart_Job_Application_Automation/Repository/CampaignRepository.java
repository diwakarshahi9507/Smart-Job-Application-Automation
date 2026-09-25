package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository
        extends JpaRepository<Campaign, Long> {

    List<Campaign> findTop5ByOrderByCreatedTimeDesc();

}