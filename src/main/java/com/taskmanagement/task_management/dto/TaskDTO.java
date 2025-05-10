package com.taskmanagement.task_management.dto;

 

import com.taskmanagement.task_management.model.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TaskDTO {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        private String description;
        
        private TaskStatus status;
        
        private Long assignedUserId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime lastModified;
        private TaskStatus status;
        private UserDTO assignedUser;
        private UserDTO createdBy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskUpdateRequest {
        private String title;
        private String description;
        private TaskStatus status;
        private Long assignedUserId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskSearchCriteria {
        private String keyword;
        private TaskStatus status;
        private Long userId;
        private int page = 0;
        private int size = 10;
        private String sortBy = "createdAt";
        private String sortDirection = "desc";
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskNotification {
        @NotNull
        private Long taskId;
        
        @NotNull
        private String title;
        
        private String message;
        
        @NotNull
        private Long userId;
        
        private String type = "TASK_ASSIGNED";
        
        private LocalDateTime timestamp = LocalDateTime.now();
    }
}

