package com.taskmanagement.task_management.config;
 
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import com.taskmanagement.task_management.security.JwtAuthenticationFilter;
import com.taskmanagement.task_management.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
  


      
    private final UserDetailsServiceImpl userDetailsServiceImpl; // Añadir la dependencia

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
       
    //         .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(authorize -> authorize
    //             .requestMatchers("/api/auth/**").permitAll()              
    //             .requestMatchers("/h2-console/**").permitAll()
    //             .requestMatchers("/actuator/**").permitAll()
    //             .requestMatchers("/ws/**","/ws-test/**","/topic/**", "/app/**").permitAll()
    //         );

    //     // Allow frames for H2 console
    //     http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
    //     http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()              
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/ws/**","/ws-test/**","/topic/**", "/app/**").permitAll()
                .anyRequest().authenticated()
            );

        // Allow frames for H2 console
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



      @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration generalConfig = new CorsConfiguration();
        generalConfig.setAllowedOrigins(List.of("*"));
        generalConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        generalConfig.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        generalConfig.setExposedHeaders(List.of("x-auth-token"));

        
        CorsConfiguration wsConfig = new CorsConfiguration();
        wsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        wsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        wsConfig.setAllowCredentials(true);
        wsConfig.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/ws/**", wsConfig);
        source.registerCorsConfiguration("/topic/**", wsConfig);
        source.registerCorsConfiguration("/app/**", wsConfig);
        source.registerCorsConfiguration("/**", generalConfig);
    return source;
    }

 


 
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    //     config.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
    //     config.setAllowCredentials(true);
    //     config.setAllowedHeaders(Arrays.asList("*"));
        
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return source;
    // }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

      @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}