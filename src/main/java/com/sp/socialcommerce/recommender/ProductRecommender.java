package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

/**
 * Class responsible for recommending products to new users.
 *
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Service
public class ProductRecommender {

    @Qualifier("graphDBManager")
    @Autowired
    GraphDBManager GDBM;

    public List<Product> getUserHighestRatedProductsSortedDescendigly(String userId, int howMany) {
        Map<Integer, String> productRatings = GDBM.getUserProductRatings(userId);
        productRatings = sortProductsDescendingWithRating(productRatings);
        List<Product> productRatingsSortedList = new ArrayList<>();

        int counter = 0;
        for(Map.Entry<Integer, String> entry : productRatings.entrySet()) {
            if(counter <= howMany) {
                counter++;
                Product product = GDBM.parseProduct(GDBM.getNode(new Product(), GraphConstants.Product.PRODUCT_ID, entry.getKey().toString()));
                product.setRating(entry.getValue().toString());
                productRatingsSortedList.add(product);
            } else break;
        }

        return productRatingsSortedList;
    }

    public List<Product> getUserHighestRatedProducts(String userId, int ratingLowest) {
        Map<Integer, String> productRatings = GDBM.getUserProductRatings(userId);
        List<Product> productRatingsFilteredList = new ArrayList<>();

        for(Map.Entry<Integer, String> entry : productRatings.entrySet()) {
            if(Integer.parseInt(entry.getValue().toString()) >= ratingLowest) {
                Product product = GDBM.parseProduct(GDBM.getNode(new Product(), GraphConstants.Product.PRODUCT_ID, entry.getKey().toString()));
                product.setRating(entry.getValue().toString());
                productRatingsFilteredList.add(product);
            }
        }

        return productRatingsFilteredList;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortProductsDescendingWithRating( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Comparator.comparing(e -> Integer.parseInt(e.getValue().toString()))))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

}
