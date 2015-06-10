package com.sp.socialcommerce.gigya;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphDBManager;

/**
 * Class responsible for handling products rating process.
 * Via the <code>GraphDBManager</code> it will fetch products to be rated.
 * Then it will also update product ratings in the DB.
 */
@Service
public class ProductRatingsService {

    @Autowired
    private GraphDBManager GDBM;

    public List<Product> getProducts() {
        return GDBM.getAllProducts();
    }

    public List<Product> getProducts(String uid) {
        return GDBM.getAllProducts(uid);
    }
    
    public Map<String, List<Product>> getProductsByCategories(String uid) {
    	return GDBM.getAllProductsByCategories(uid);
    }

    public void setProductRating(String uid, String productId, String score) {
        GDBM.setProductRating(uid, productId, score);
    }

}
