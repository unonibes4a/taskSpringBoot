package com.taskmanagement.task_management.controller;

import com.taskmanagement.task_management.dto.UserDTO;
import com.taskmanagement.task_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Obtener todos los usuarios (solo ADMIN)
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Obtener el perfil del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    // Asignar rol ADMIN a un usuario (solo ADMIN)
    @PostMapping("/{id}/assign-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> assignAdminRole(@PathVariable Long id) {
        userService.assignAdminRole(id);
        return ResponseEntity.ok().build();
    }

    // Actualizar el idioma preferido del usuario
    @PutMapping("/{id}/language")
    public ResponseEntity<Void> updatePreferredLanguage(@PathVariable Long id, @RequestParam String language) {
        userService.updatePreferredLanguage(id, language);
        return ResponseEntity.ok().build();
    }
}
