package com.taskmanagement.task_management.service;

import com.taskmanagement.task_management.dto.TaskDTO;
import com.taskmanagement.task_management.dto.TaskDTO.TaskNotification;
import com.taskmanagement.task_management.dto.TaskDTO.TaskRequest;
import com.taskmanagement.task_management.dto.TaskDTO.TaskSearchCriteria;
import com.taskmanagement.task_management.dto.TaskDTO.TaskUpdateRequest;
import com.taskmanagement.task_management.dto.UserDTO;
import com.taskmanagement.task_management.exception.ResourceNotFoundException;
import com.taskmanagement.task_management.model.Task;
import com.taskmanagement.task_management.model.Task.TaskStatus;
import com.taskmanagement.task_management.model.User;
import com.taskmanagement.task_management.repository.TaskRepository;
import com.taskmanagement.task_management.repository.UserRepository;
import com.taskmanagement.task_management.response.TaskResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TaskDTO.TaskResponse createTask(TaskRequest taskRequest) {
        User currentUser = authService.getCurrentUser();

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCreatedBy(currentUser);

        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        User    assignedUser = userRepository.findById(taskRequest.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskRequest.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
            task.setCreatedBy(currentUser);  

           
            TaskNotification notification1 = new TaskNotification(
                        task.getId(),
                        task.getTitle(),
                        "Se le ha asignado una tarea por " + currentUser.getName(),
                        task.getAssignedUser().getId(),
                        task.getStatus().name(),
                        task.getCreatedAt()
                );
            messagingTemplate.convertAndSend("/topic/user/" + assignedUser.getId() + "/notifications", notification1);
            
            if (!assignedUser.getId().equals(currentUser.getId())) {
                TaskNotification notification2 = new TaskNotification(
                    task.getId(),
                    task.getTitle(),
                    "le asignaste una tarea a " + assignedUser.getName(),
                    task.getAssignedUser().getId(),
                    task.getStatus().name(),
                    task.getCreatedAt()
                 );
                messagingTemplate.convertAndSend("/topic/user/" + currentUser.getId() + "/notifications", notification2);   
            }

        Task savedTask = taskRepository.save(task);


        return new TaskDTO.TaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getCreatedAt(),
                savedTask.getLastModified(),
                savedTask.getStatus(),
                new UserDTO(assignedUser.getId(), assignedUser.getName(), assignedUser.getEmail()),
                new UserDTO(currentUser.getId(), currentUser.getName(), currentUser.getEmail())
        );
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public TaskDTO.TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
        User currentUser = authService.getCurrentUser();
        Task task = getTaskById(id);

        Long oldAssignedUserId = task.getAssignedUser() != null ? task.getAssignedUser().getId() : null;
        Long newAssignedUserId = taskUpdateRequest.getAssignedUserId();

        if (StringUtils.hasText(taskUpdateRequest.getTitle())) {
            task.setTitle(taskUpdateRequest.getTitle());
        }

        if (StringUtils.hasText(taskUpdateRequest.getDescription())) {
            task.setDescription(taskUpdateRequest.getDescription());
        }

        if (taskUpdateRequest.getStatus() != null) {
            task.setStatus(taskUpdateRequest.getStatus());
        }

        if (newAssignedUserId != null && (oldAssignedUserId == null || !oldAssignedUserId.equals(newAssignedUserId))) {
            User assignedUser = userRepository.findById(newAssignedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + newAssignedUserId));
            task.setAssignedUser(assignedUser);

            if (!assignedUser.getId().equals(currentUser.getId())) {
                TaskNotification notification1 = new TaskNotification(
                        task.getId(),
                        task.getTitle(),
                        "Se actualizado la tarea "+task.getTitle() +" con #"+task.getId(),
                        task.getAssignedUser().getId(),
                        task.getStatus().name(),
                        task.getCreatedAt()
                );
                messagingTemplate.convertAndSend("/topic/user/" + assignedUser.getId() + "/notifications", notification1);
            }
        }

        log.info("Updating task: {}", task.getTitle());
        Task updated = taskRepository.save(task);
        log.info("Updating task: {}", updated.getTitle());
    
        return convertToTaskResponse(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        log.info("Deleting task: {}", task.getTitle());
        taskRepository.delete(task);
    }

    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks;

        if (StringUtils.hasText(criteria.getKeyword())) {
            if (criteria.getStatus() != null) {
                tasks = taskRepository.searchByStatusAndKeyword(criteria.getStatus(), criteria.getKeyword());
            } else if (criteria.getUserId() != null) {
                User user = userRepository.findById(criteria.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + criteria.getUserId()));
                tasks = taskRepository.searchByUserAndKeyword(user, criteria.getKeyword());
            } else {
                tasks = taskRepository.searchByKeyword(criteria.getKeyword());
            }
        } else if (criteria.
        getStatus() != null) {
            tasks = taskRepository.findByStatus(criteria.getStatus());
        } else if (criteria.getUserId() != null) {
            User user = userRepository.findById(criteria.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + criteria.getUserId()));
            tasks = taskRepository.findByAssignedUser(user);
        } else {
            tasks = taskRepository.findAll();
        }
      
        return tasks;
    }

    public List<TaskDTO.TaskResponse> getTasksForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks = taskRepository.findByCreatedBy(currentUser);
        
        return tasks.stream().map(this::convertToTaskResponse).toList();
    }

    public List<TaskDTO.TaskResponse> getTasksForAssignedUser() {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks = taskRepository.findByAssignedUser(currentUser);
        
        return tasks.stream().map(this::convertToTaskResponse).toList();
    }

    

    private TaskDTO.TaskResponse convertToTaskResponse(Task task) {
        UserDTO assignedUserDTO = new UserDTO(
            task.getAssignedUser().getId(),
            task.getAssignedUser().getName(), // o el campo que uses
            task.getAssignedUser().getEmail()
        );
    
        UserDTO createdByDTO = new UserDTO(
            task.getCreatedBy().getId(),
            task.getCreatedBy().getName(),
            task.getCreatedBy().getEmail()
        );
    
        return new TaskDTO.TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getLastModified(),
            task.getStatus(),
            assignedUserDTO,
            createdByDTO
        );
    }
    

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
