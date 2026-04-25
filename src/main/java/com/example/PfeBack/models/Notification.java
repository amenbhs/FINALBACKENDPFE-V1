package com.example.PfeBack.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String title;
    private String message;
    private String type; // animal, plant, weather
    private String farmId; // Add farmId field
    private LocalDateTime createdAt;
    private boolean read;

    public Notification() {}

    public Notification(String title, String message, String type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Notification(String title, String message, String type, String farmId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.farmId = farmId;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFarmId() { return farmId; }
    public void setFarmId(String farmId) { this.farmId = farmId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
