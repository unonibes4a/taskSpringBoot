package com.taskmanagement.task_management.response;

import java.time.LocalDateTime;

import com.taskmanagement.task_management.model.Task;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    // Constructor
    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        this.createdAt = task.getCreatedAt();
        this.lastModified = task.getLastModified();
    }

    // Getters y setters si no usas Lombok
}
