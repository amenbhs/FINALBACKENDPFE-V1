package com.example.PfeBack.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.PfeBack.models.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
