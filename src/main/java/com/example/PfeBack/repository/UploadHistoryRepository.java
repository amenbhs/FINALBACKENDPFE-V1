package com.example.PfeBack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.PfeBack.models.UploadHistory;

import java.util.List;

@Repository
public interface UploadHistoryRepository extends MongoRepository<UploadHistory, String> {
    List<UploadHistory> findByUserId(String userId);
}