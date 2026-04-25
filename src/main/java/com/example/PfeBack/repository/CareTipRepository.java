package com.example.PfeBack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.PfeBack.models.CareTip;

@Repository
public interface CareTipRepository extends MongoRepository<CareTip, String> {
}
