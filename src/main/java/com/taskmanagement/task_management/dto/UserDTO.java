package com.taskmanagement.task_management.dto;


 

import com.taskmanagement.task_management.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles;
    private String preferredLanguage;
    
    // Constructor without tasks to avoid circular references
    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
