package com.taskmanagement.task_management.controller;

import com.taskmanagement.task_management.dto.TaskDTO;
import com.taskmanagement.task_management.dto.TaskDTO.TaskSearchCriteria;
import com.taskmanagement.task_management.dto.TaskDTO.TaskUpdateRequest;
import com.taskmanagement.task_management.model.Task;
import com.taskmanagement.task_management.model.Task.TaskStatus;
import com.taskmanagement.task_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO.TaskResponse> createTask(@RequestBody TaskDTO.TaskRequest request) {
        TaskDTO.TaskResponse created = taskService.createTask(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest updateRequest) {
        TaskDTO.TaskResponse updated = taskService.updateTask(id, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestBody TaskSearchCriteria criteria) {
        List<Task> results = taskService.searchTasks(criteria);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksForCurrentUser() {
        List<TaskDTO.TaskResponse> tasks = taskService.getTasksForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksForAssignedUser() {
        List<TaskDTO.TaskResponse> tasks = taskService.getTasksForAssignedUser();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
}
