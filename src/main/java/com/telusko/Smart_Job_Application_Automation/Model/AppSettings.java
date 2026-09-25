package com.telusko.Smart_Job_Application_Automation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;

    private String senderEmail;

    private String resumePath;

    private String defaultPosition;

    @Column(columnDefinition = "TEXT")
    private String emailSignature;
}