package com.taskmanagement.task_management.controller;

import com.taskmanagement.task_management.dto.AuthDTO.JwtResponse;
import com.taskmanagement.task_management.dto.AuthDTO.LoginRequest;
import com.taskmanagement.task_management.dto.AuthDTO.SignupRequest;
import com.taskmanagement.task_management.model.User;
import com.taskmanagement.task_management.security.JwtUtils;
import com.taskmanagement.task_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
 private final SimpMessagingTemplate messagingTemplate;
    private final AuthService authService;
    @GetMapping("/notificacin")
    public ResponseEntity<String> notificacion() {
        messagingTemplate.convertAndSend("/topic/user/1/notifications", "probando ando");
        return ResponseEntity.ok("validando respuesta");
    }

    // Endpoint para registrar nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody SignupRequest signupRequest) {
        User newUser = authService.registerUser(signupRequest);
    
        // Reusar el objeto de login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(signupRequest.getEmail());
        loginRequest.setPassword(signupRequest.getPassword());
    
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    // Endpoint para login y generación del JWT
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> getValidate() {
        return ResponseEntity.ok("validando respuesta");
    }

    // Endpoint opcional para obtener el usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
