package com.telusko.Smart_Job_Application_Automation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campaignName;

    private String position;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private EmailTemplateEntity template;

    private Integer recruiterCount;

    private String status;

    private LocalDateTime createdTime;
}