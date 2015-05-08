package com.sp.socialcommerce.neo4j.dao;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.sp.socialcommerce.neo4j.domain.Product;

public interface ProductRepository extends GraphRepository<Product>{ 
}
