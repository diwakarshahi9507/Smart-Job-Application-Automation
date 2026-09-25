package com.telusko.Smart_Job_Application_Automation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(
            name = "campaign_id",
            nullable = false
    )
    private Campaign campaign;


    @ManyToOne
    @JoinColumn(
            name = "recruiter_id",
            nullable = false
    )
    private Recruiter recruiter;


    private String status;


    private LocalDateTime sentTime;

    private LocalDateTime attemptTime;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}