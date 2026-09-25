package com.telusko.Smart_Job_Application_Automation.Repository;

import com.telusko.Smart_Job_Application_Automation.Model.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository
        extends JpaRepository<AppSettings, Long> {
}