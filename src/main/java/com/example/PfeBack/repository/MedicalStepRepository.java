package com.example.PfeBack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.PfeBack.models.MedicalStep;


public interface MedicalStepRepository extends MongoRepository<MedicalStep, String> {}
