package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.AppSettings;
import com.telusko.Smart_Job_Application_Automation.Repository.AppSettingsRepository;

import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final AppSettingsRepository appSettingsRepository;

    public SettingsService(
            AppSettingsRepository appSettingsRepository
    ) {
        this.appSettingsRepository = appSettingsRepository;
    }


    public AppSettings getSettings() {

        return appSettingsRepository
                .findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {

                    AppSettings settings =
                            new AppSettings();

                    settings.setSenderName("");
                    settings.setSenderEmail("");
                    settings.setResumePath("");
                    settings.setDefaultPosition("");
                    settings.setEmailSignature("");

                    return appSettingsRepository.save(
                            settings
                    );
                });
    }


    public AppSettings saveSettings(
            AppSettings updatedSettings
    ) {

        AppSettings existingSettings =
                getSettings();

        existingSettings.setSenderName(
                updatedSettings.getSenderName()
        );

        existingSettings.setSenderEmail(
                updatedSettings.getSenderEmail()
        );

        existingSettings.setResumePath(
                updatedSettings.getResumePath()
        );

        existingSettings.setDefaultPosition(
                updatedSettings.getDefaultPosition()
        );

        existingSettings.setEmailSignature(
                updatedSettings.getEmailSignature()
        );

        return appSettingsRepository.save(
                existingSettings
        );
    }
}