package com.example.PfeBack.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.PfeBack.Services.NotificationService;
import com.example.PfeBack.models.Notification;
import com.example.PfeBack.repository.NotificationRepository;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    // GET all notifications (sorted newest first)
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            return ResponseEntity.ok(notificationService.getAllNotifications());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET unread only
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        try {
            return ResponseEntity.ok(notificationService.getUnreadNotifications());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create a single notification
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            if (notification.getSeverity() == null) notification.setSeverity("low");
            return ResponseEntity.ok(notificationRepository.save(notification));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST generate NEW dynamic notifications based on real farm data (does NOT wipe existing)
    @PostMapping("/generate-samples")
    public ResponseEntity<String> generateDynamic() {
        try {
            int count = notificationService.generateRealNotifications();
            return ResponseEntity.ok("Generated " + count + " new notifications based on your farm data.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error generating notifications: " + e.getMessage());
        }
    }

    // PUT mark single notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            if (notification != null) return ResponseEntity.ok(notification);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT mark all as read
    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok("All notifications marked as read.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    // DELETE single notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        try {
            notificationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE all notifications
    @DeleteMapping
    public ResponseEntity<String> deleteAll() {
        try {
            notificationRepository.deleteAll();
            return ResponseEntity.ok("All notifications deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}