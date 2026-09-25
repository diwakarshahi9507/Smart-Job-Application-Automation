package com.telusko.Smart_Job_Application_Automation.Controller;

import com.telusko.Smart_Job_Application_Automation.Model.EmailTemplateEntity;
import com.telusko.Smart_Job_Application_Automation.Service.EmailTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TemplateController {

    private final EmailTemplateService emailTemplateService;

    public TemplateController(
            EmailTemplateService emailTemplateService
    ) {
        this.emailTemplateService = emailTemplateService;
    }


    @GetMapping("/templates")
    public String templates(Model model) {

        model.addAttribute(
                "templates",
                emailTemplateService.getActiveTemplates()
        );

        model.addAttribute(
                "template",
                new EmailTemplateEntity()
        );

        return "templates";
    }


    @PostMapping("/templates/create")
    public String createTemplate(
            @ModelAttribute EmailTemplateEntity template,
            Model model
    ) {

        try {

            emailTemplateService.createTemplate(template);

            model.addAttribute(
                    "success",
                    "Template created successfully."
            );

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );
        }

        model.addAttribute(
                "templates",
                emailTemplateService.getActiveTemplates()
        );

        model.addAttribute(
                "template",
                new EmailTemplateEntity()
        );

        return "templates";
    }


    @GetMapping("/templates/edit/{id}")
    public String editTemplate(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "template",
                emailTemplateService.getTemplateById(id)
        );

        return "template-edit";
    }


    @PostMapping("/templates/update")
    public String updateTemplate(
            @ModelAttribute EmailTemplateEntity template,
            Model model
    ) {

        try {

            emailTemplateService.updateTemplate(
                    template
            );

            return "redirect:/templates";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "template",
                    template
            );

            return "template-edit";
        }
    }


    @PostMapping("/templates/delete/{id}")
    public String deleteTemplate(
            @PathVariable Long id
    ) {

        emailTemplateService.deleteTemplate(id);

        return "redirect:/templates";
    }
}