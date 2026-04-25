package com.example.PfeBack.Services;

import com.example.PfeBack.models.UploadHistory;
import com.example.PfeBack.repository.UploadHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadHistoryService {

    @Autowired
    private UploadHistoryRepository uploadHistoryRepository;

    public UploadHistory saveUploadHistory(UploadHistory uploadHistory) {
        return uploadHistoryRepository.save(uploadHistory);
    }

    public List<UploadHistory> getUploadHistoryByUserId(String userId) {
        return uploadHistoryRepository.findByUserId(userId);
    }
}