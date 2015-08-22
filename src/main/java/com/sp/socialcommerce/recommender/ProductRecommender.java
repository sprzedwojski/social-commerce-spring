package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * Class responsible for recommending products to new users.
 *
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
public class ProductRecommender {

    @Autowired
    GraphDBManager GDBM;

    private List<Product> getUserHighestRatedProductsSortedDescendigly(String userId, int howMany) {
        List<Product> products = GDBM.getAllProducts(userId);

        /*return Collections.sort(products);*/
        return null;
    }

}
