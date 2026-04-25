package com.example.PfeBack.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.PfeBack.models.Disease;

import java.util.List;

@Repository
public interface DiseaseRepository extends MongoRepository<Disease, String> {
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Disease> findByNameContaining(String keyword);

}
