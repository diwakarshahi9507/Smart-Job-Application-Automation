package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Model.AppSettings;
import com.telusko.Smart_Job_Application_Automation.Service.SettingsService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {

        AppSettings settings =
                settingsService.getSettings();

        model.addAttribute(
                "settings",
                settings
        );

        return "settings";
    }

    @PostMapping("/settings")
    public String saveSettings(
            @ModelAttribute("settings")
            AppSettings settings,
            Model model
    ) {

        try {

            settingsService.saveSettings(settings);

            model.addAttribute(
                    "success",
                    "Settings saved successfully."
            );

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Unable to save settings: "
                            + e.getMessage()
            );
        }

        model.addAttribute(
                "settings",
                settingsService.getSettings()
        );

        return "settings";
    }

    @PostMapping("/settings/upload-resume")
    public String uploadResume(
            @RequestParam("resumeFile")
            MultipartFile resumeFile,
            Model model
    ) {

        try {


            if (resumeFile == null
                    || resumeFile.isEmpty()) {

                model.addAttribute(
                        "error",
                        "Please select a resume file."
                );

                model.addAttribute(
                        "settings",
                        settingsService.getSettings()
                );

                return "settings";
            }

            String originalFileName =
                    resumeFile.getOriginalFilename();

            if (originalFileName == null
                    || originalFileName.isBlank()) {

                model.addAttribute(
                        "error",
                        "Invalid resume file."
                );

                model.addAttribute(
                        "settings",
                        settingsService.getSettings()
                );

                return "settings";
            }

            originalFileName =
                    Paths.get(originalFileName)
                            .getFileName()
                            .toString();


            if (!originalFileName
                    .toLowerCase()
                    .endsWith(".pdf")) {

                model.addAttribute(
                        "error",
                        "Only PDF resume files are allowed."
                );

                model.addAttribute(
                        "settings",
                        settingsService.getSettings()
                );

                return "settings";
            }


            Path uploadDirectory =
                    Paths.get(
                            "uploads",
                            "resumes"
                    );

            Files.createDirectories(
                    uploadDirectory
            );


            String newFileName =
                    System.currentTimeMillis()
                            + "_"
                            + originalFileName;

            Path newResumePath =
                    uploadDirectory
                            .resolve(newFileName)
                            .toAbsolutePath()
                            .normalize();



            resumeFile.transferTo(
                    newResumePath
            );

            AppSettings settings =
                    settingsService.getSettings();

            String oldResumePath =
                    settings.getResumePath();


            settings.setResumePath(
                    newResumePath.toString()
            );

            settingsService.saveSettings(
                    settings
            );



            if (oldResumePath != null
                    && !oldResumePath.isBlank()) {

                Path oldFile =
                        Paths.get(oldResumePath);


                if (!oldFile.toAbsolutePath()
                        .normalize()
                        .equals(newResumePath)) {

                    try {

                        Files.deleteIfExists(
                                oldFile
                        );

                    } catch (IOException e) {

                        System.out.println(
                                "Old resume could not be deleted: "
                                        + e.getMessage()
                        );
                    }
                }
            }


            model.addAttribute(
                    "success",
                    "Resume uploaded successfully."
            );


        } catch (IOException e) {

            model.addAttribute(
                    "error",
                    "Resume upload failed: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Something went wrong: "
                            + e.getMessage()
            );
        }


        model.addAttribute(
                "settings",
                settingsService.getSettings()
        );

        return "settings";
    }
}