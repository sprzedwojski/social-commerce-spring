package com.sp.socialcommerce.gigya;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

//    private Map<String, List<Product>> productsByCategories = null;

    public List<Product> getProducts() {
        return GDBM.getAllProducts();
    }

    public List<Product> getProducts(String uid) {
        return GDBM.getAllProducts(uid);
    }

    public Map<String, List<Product>> getProductsByCategories(String uid) {
        return GDBM.getAllProductsByCategories(uid);
    }

    public Map<String, List<Product>> getProductsByCategories(String uid, String[] categories) {
//        if(productsByCategories == null) {
        Map<String, List<Product>> productsByCategories;
        productsByCategories = GDBM.getAllProductsByCategories(uid);
        filterCategories(productsByCategories, categories);
        System.out.println("filtered categories: " + productsByCategories.toString());
//        }
    	return productsByCategories;
    }

    public void setProductRating(String uid, String productId, String score) {
        GDBM.setProductRating(uid, productId, score);
    }

    private void filterCategories(Map<String, List<Product>> productMap, String[] categories) {
        Iterator it = productMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key = (String) pair.getKey();
            boolean found = false;
            for(String category : categories) {
                if (key.compareToIgnoreCase(category) == 0) {
                    found = true;
                    break;
                }
            }

            // Removing the category from the map if not found in the user choices
            if(!found) {
                it.remove();
            }
        }
    }

}
