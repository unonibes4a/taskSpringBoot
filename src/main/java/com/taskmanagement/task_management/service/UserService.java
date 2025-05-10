package com.taskmanagement.task_management.service;



 

import com.taskmanagement.task_management.dto.UserDTO;
import com.taskmanagement.task_management.exception.ResourceNotFoundException;
import com.taskmanagement.task_management.model.User;
import com.taskmanagement.task_management.model.User.Role;
import com.taskmanagement.task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public List<UserDTO> getAllUsers() {
        User currentUser = authService.getCurrentUser();
    
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUser.getId())) // excluye al usuario actual
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void assignAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Set<Role> roles = user.getRoles();
        roles.add(Role.ROLE_ADMIN);
        user.setRoles(roles);
        
        log.info("Assigning ADMIN role to user: {}", user.getEmail());
        userRepository.save(user);
    }

    @Transactional
    public void updatePreferredLanguage(Long userId, String language) {
        User currentUser = authService.getCurrentUser();
        
        // Only allow users to update their own language preference or admins to update any
        if (!currentUser.getId().equals(userId) && 
            !currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new IllegalArgumentException("You can only update your own language preference");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setPreferredLanguage(language);
        log.info("Updating preferred language for user {}: {}", user.getEmail(), language);
        userRepository.save(user);
    }
    
    public UserDTO getCurrentUserProfile() {
        User currentUser = authService.getCurrentUser();
        return convertToDTO(currentUser);
    }
    
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            user.getPreferredLanguage()
        );
    }
}
