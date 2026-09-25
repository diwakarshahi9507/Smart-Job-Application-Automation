package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import com.telusko.Smart_Job_Application_Automation.Service.EmailService;
import com.telusko.Smart_Job_Application_Automation.Service.EmailTemplateService;
import com.telusko.Smart_Job_Application_Automation.Service.ExcelService;
import com.telusko.Smart_Job_Application_Automation.Service.RecruiterService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/recruiters")
public class RecruiterController {

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final ExcelService excelService;
    private final RecruiterService recruiterService;


    public RecruiterController(
            EmailService emailService,
            EmailTemplateService emailTemplateService,
            ExcelService excelService,
            RecruiterService recruiterService
    ) {
        this.emailService = emailService;
        this.emailTemplateService = emailTemplateService;
        this.excelService = excelService;
        this.recruiterService = recruiterService;
    }


    @GetMapping
    public String recruiters(Model model) {

        model.addAttribute(
                "recruiters",
                recruiterService.getAllRecruiters()
        );

        model.addAttribute(
                "templates",
                emailTemplateService.getActiveTemplates()
        );

        return "recruiters";
    }

    @PostMapping("/import")
    public String importRecruiters(
            @RequestParam("file") MultipartFile file,
            Model model
    ) throws Exception {

        if (file.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please select an Excel file."
            );

            model.addAttribute(
                    "recruiters",
                    recruiterService.getAllRecruiters()
            );

            model.addAttribute(
                    "templates",
                    emailTemplateService.getActiveTemplates()
            );

            return "recruiters";
        }


        List<Recruiter> recruiters =
                excelService.readRecruiters(file);

        List<Recruiter> savedRecruiters =
                recruiterService.saveRecruiters(
                        recruiters
                );


        model.addAttribute(
                "recruiters",
                recruiterService.getAllRecruiters()
        );

        model.addAttribute(
                "templates",
                emailTemplateService.getActiveTemplates()
        );

        model.addAttribute(
                "success",
                savedRecruiters.size()
                        + " new recruiters imported successfully."
        );

        return "recruiters";
    }

    @PostMapping("/send-selected")
    public String sendSelected(
            @RequestParam(
                    name = "selectedIds",
                    required = false
            )
            List<Long> selectedIds,

            @RequestParam("templateId")
            Long templateId,

            Model model
    ) {

        if (selectedIds == null
                || selectedIds.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please select at least one recruiter."
            );

            model.addAttribute(
                    "recruiters",
                    recruiterService.getAllRecruiters()
            );

            model.addAttribute(
                    "templates",
                    emailTemplateService.getActiveTemplates()
            );

            return "recruiters";
        }


        List<Recruiter> recruiters =
                recruiterService.getRecruitersByIds(
                        selectedIds
                );


        EmailTemplateEntity template =
                emailTemplateService.getTemplateById(
                        templateId
                );


        int sentCount =
                emailService.sendEmailWithDbTemplate(
                        recruiters,
                        template
                );


        int failedCount =
                recruiters.size()
                        - sentCount;


        model.addAttribute(
                "success",
                sentCount
                        + " emails sent successfully. "
                        + failedCount
                        + " failed."
        );

        model.addAttribute(
                "recruiters",
                recruiterService.getAllRecruiters()
        );

        model.addAttribute(
                "templates",
                emailTemplateService.getActiveTemplates()
        );


        return "recruiters";
    }


    @PostMapping("/delete/{id}")
    public String deleteRecruiter(
            @PathVariable Long id
    ) {

        recruiterService.deleteRecruiter(id);

        return "redirect:/recruiters";
    }
}