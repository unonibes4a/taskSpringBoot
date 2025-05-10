package com.taskmanagement.task_management.dto; 
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignupRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        private String name;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private String[] roles;
        
        public JwtResponse(String token, Long id, String name, String email, String[] roles) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
            this.roles = roles;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}