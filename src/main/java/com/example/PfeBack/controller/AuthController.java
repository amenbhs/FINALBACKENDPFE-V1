package com.example.PfeBack.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Security.JwtUtil;
import com.example.PfeBack.models.AuthRequest;
import com.example.PfeBack.models.User;
import com.example.PfeBack.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }
        user.setFarm(null);
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of("ADMIN"));
        userRepository.save(user);
        return "Admin registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent() && passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            User user = userOptional.get();
            return jwtUtil.generateToken(user.getEmail(), new ArrayList<>(user.getRoles()));
        }
        return "Invalid credentials!";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody EmailRequest emailRequest) {
        Optional<User> userOptional = userRepository.findByEmail(emailRequest.getEmail());
        if (!userOptional.isPresent()) {
            return "Email not found!";
        }

        User user = userOptional.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(new Date(System.currentTimeMillis() + 3600000)); // 1-hour expiry
        userRepository.save(user);

        String resetLink = "http://localhost:5174/reset-password?token=" + resetToken;
        sendResetEmail(user.getEmail(), resetLink);

        return "Password reset link sent to your email!";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByResetToken(request.getToken());
        if (!userOptional.isPresent() || userOptional.get().getResetTokenExpiry().before(new Date())) {
            return "Invalid or expired token!";
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return "Password reset successfully!";
    }

    @GetMapping("/user")
    public Map<String, Object> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired JWT token");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());
        userDetails.put("roles", new ArrayList<>(user.getRoles())); // Include roles in the response
        return userDetails;
    }

    private void sendResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetLink);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            System.out.println("Reset link (for testing): " + resetLink);
        }
    }
}


class EmailRequest {
    private String email;

    public EmailRequest() {}

    public EmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}