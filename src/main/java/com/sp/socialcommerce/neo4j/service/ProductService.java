package com.sp.socialcommerce.neo4j.service;

import org.springframework.data.neo4j.conversion.Result;

import com.sp.socialcommerce.neo4j.domain.Product;


public interface ProductService {

   Product create(Product profile);
   void delete(Product profile);		
   Product findById(long id);		
   Result<Product> findAll();
}
