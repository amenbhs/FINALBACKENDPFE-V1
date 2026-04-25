package com.example.PfeBack.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.Map;

@Document(collection = "upload_history")
public class UploadHistory {
    @Id
    private String id;
    private String userId;
    private String imageBase64;
    private Map<String, Object> detectionResult;
    private Date timestamp;

    public UploadHistory() {}

    public UploadHistory(String id, String userId, String imageBase64, Map<String, Object> detectionResult, Date timestamp) {
        this.id = id;
        this.userId = userId;
        this.imageBase64 = imageBase64;
        this.detectionResult = detectionResult;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Map<String, Object> getDetectionResult() {
        return detectionResult;
    }

    public void setDetectionResult(Map<String, Object> detectionResult) {
        this.detectionResult = detectionResult;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}