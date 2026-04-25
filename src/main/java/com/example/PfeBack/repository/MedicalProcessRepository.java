package com.example.PfeBack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.PfeBack.models.MedicalProcess;

public interface MedicalProcessRepository extends MongoRepository<MedicalProcess, String> {}
