package com.taskmanagement.task_management.model;


 

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignedUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified = LocalDateTime.now();
    
    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
    
    @PreUpdate
    public void preUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}