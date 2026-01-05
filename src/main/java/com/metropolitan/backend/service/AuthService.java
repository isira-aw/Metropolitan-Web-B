package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.AuthResponse;
import com.metropolitan.backend.dto.LoginRequest;
import com.metropolitan.backend.dto.RegisterRequest;
import com.metropolitan.backend.model.AdminUser;
import com.metropolitan.backend.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final int MAX_ADMIN_COUNT = 2;

    public AuthResponse register(RegisterRequest request) {
        // Check if already 2 admins exist
        long adminCount = adminUserRepository.count();
        if (adminCount >= MAX_ADMIN_COUNT) {
            throw new RuntimeException("Maximum number of admin users (2) reached. Registration not allowed.");
        }

        // Check if email already exists
        if (adminUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new admin user
        AdminUser admin = new AdminUser();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setIsActive(true);

        AdminUser savedAdmin = adminUserRepository.save(admin);

        // Generate JWT token
        String token = jwtService.generateToken(savedAdmin.getEmail());

        return new AuthResponse(
                token,
                savedAdmin.getId(),
                savedAdmin.getName(),
                savedAdmin.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by email
        AdminUser admin = adminUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if admin is active
        if (!admin.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Update last login
        admin.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(admin);

        // Generate JWT token
        String token = jwtService.generateToken(admin.getEmail());

        return new AuthResponse(
                token,
                admin.getId(),
                admin.getName(),
                admin.getEmail()
        );
    }

    public AdminUser getAdminByEmail(String email) {
        return adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }
}
