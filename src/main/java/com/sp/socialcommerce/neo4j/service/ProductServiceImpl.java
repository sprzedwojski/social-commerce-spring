package com.sp.socialcommerce.neo4j.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;

import com.sp.socialcommerce.neo4j.dao.ProductRepository;
import com.sp.socialcommerce.neo4j.domain.Product;


@Service("ProductService")
public class ProductServiceImpl implements ProductService {

   @Autowired
   private ProductRepository ProductRepository;	

   public Product create(Product profile) {
      return ProductRepository.save(profile);
   }

   public void delete(Product profile) {		
      ProductRepository.delete(profile);
   }

   public Product findById(long id) {		
      return ProductRepository.findOne(id);
   }

   public Result<Product> findAll() {		
      return ProductRepository.findAll();
   }
}
