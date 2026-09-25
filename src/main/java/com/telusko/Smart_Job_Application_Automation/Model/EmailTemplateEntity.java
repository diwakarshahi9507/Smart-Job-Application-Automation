package com.telusko.Smart_Job_Application_Automation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String body;

    private boolean active = true;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}