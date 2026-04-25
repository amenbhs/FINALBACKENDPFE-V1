package com.example.PfeBack.repository;

import com.example.PfeBack.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByReadFalse();
    List<Notification> findByType(String type);
    List<Notification> findByTypeAndReadFalse(String type);
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    // Farm-specific queries
    List<Notification> findByFarmId(String farmId);
    List<Notification> findByFarmIdAndReadFalse(String farmId);
    List<Notification> findByFarmIdOrderByCreatedAtDesc(String farmId);
}
