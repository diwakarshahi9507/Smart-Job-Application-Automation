package com.telusko.Smart_Job_Application_Automation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private EmailTemplateEntity template;

    private String position;

    private String status;

    private LocalDateTime attemptTime;

    private LocalDateTime sentTime;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public void setSubject(String subject) {
    }
}