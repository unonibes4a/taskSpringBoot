package com.taskmanagement.task_management.service;

 

import com.taskmanagement.task_management.dto.AuthDTO.JwtResponse;
import com.taskmanagement.task_management.dto.AuthDTO.LoginRequest;
import com.taskmanagement.task_management.dto.AuthDTO.SignupRequest;
import com.taskmanagement.task_management.exception.ResourceNotFoundException;
import com.taskmanagement.task_management.model.User;
import com.taskmanagement.task_management.repository.UserRepository;
import com.taskmanagement.task_management.security.JwtUtils;
import com.taskmanagement.task_management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        user.setRoles(roles);

        log.info("Registering new user: {}", signupRequest.getEmail());
        return userRepository.save(user);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String[] roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList())
                .toArray(new String[0]);

        log.info("User authenticated: {}", loginRequest.getEmail());
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getName(), userDetails.getUsername(), roles);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDetails.getId()));
    }
}
