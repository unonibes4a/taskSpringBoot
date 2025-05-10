package com.taskmanagement.task_management.repository;

import com.taskmanagement.task_management.model.Task;
import com.taskmanagement.task_management.model.Task.TaskStatus;
import com.taskmanagement.task_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedUserAndStatus(User user, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignedUser = :user AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Task> searchByUserAndKeyword(@Param("user") User user, @Param("search") String search);

    @Query("SELECT t FROM Task t WHERE " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Task> searchByKeyword(@Param("search") String search);

    @Query("SELECT t FROM Task t WHERE t.status = :status AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Task> searchByStatusAndKeyword(@Param("status") TaskStatus status, @Param("search") String search);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByAssignedUser(User user);

    List<Task> findByCreatedBy(User user);
    
}
