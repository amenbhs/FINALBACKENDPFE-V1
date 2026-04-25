package com.example.PfeBack.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Services.NotificationService;
import com.example.PfeBack.models.Notification;
import com.example.PfeBack.repository.NotificationRepository;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // React dev ports

public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
				return notificationRepository.save(notification);
    }

    @PostMapping("/generate-samples")
    public ResponseEntity<String> generateSampleNotifications() {
        try {
            notificationService.generateSampleNotifications();
            return ResponseEntity.ok("Sample notifications generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error generating sample notifications: " + e.getMessage());
        }
    }

    @PostMapping("/generate-dynamic")
    public ResponseEntity<String> generateDynamicNotifications() {
        try {
            notificationService.generateDynamicNotifications();
            return ResponseEntity.ok("Dynamic notifications generated based on current data!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error generating dynamic notifications: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            if (notification != null) {
                return ResponseEntity.ok(notification);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok("All notifications marked as read!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error marking notifications as read: " + e.getMessage());
        }
    }
}
