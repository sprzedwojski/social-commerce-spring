package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    UserSimilarityProcessor userSimilarityProcessor;

    public static final int ALL_PRODUCTS_FLAG = -1;

    public Map<Product, Double> getRecommendedProductsForUser(String userId) {
        int K = 10;
        int minNumberOfSimilarUserRatings = 4;
        int lowestRating = 4;

        List<SimilarUser> similarUserList = userSimilarityProcessor.findSimilarUsers(userId, K);

        Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
        Map<String, Double> similarUserSimilarityMap = new HashMap<>();

        similarUserList.forEach((x) -> {
            List<Product> highestRatedUserProducts = this.getUserHighestRatedProducts(x.getUserId(), lowestRating);
            similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
            similarUserSimilarityMap.put(x.getUserId(), x.getSimilaritySum());
        });

        return getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, K, minNumberOfSimilarUserRatings);
    }

    public Map<Product, Double> getRecommendedProductsForUser(Map<String, List<Product>> similarUserProductsMap, Map<String, Double> similarUserSimilarityMap,
                                                              int howMany, int minNumberOfSimilarUserRatings) {

        // <productId, List<[productRating,similarity]>>
        Map<Integer, List<Double[]>> recommendableProducts = new HashMap<>();

        Map<Integer, Product> helperProductIdMap = new HashMap<>();

        similarUserProductsMap.forEach((similarUserId, productsList) ->
            productsList.forEach(product -> {
                if(recommendableProducts.containsKey(product.getId())) {
                    recommendableProducts.get(product.getId()).add(new Double[]{Double.parseDouble(product.getRating()), similarUserSimilarityMap.get(similarUserId)});
                } else {
                    recommendableProducts.put(product.getId(), new ArrayList<Double[]>() {{
                        /*add(Integer.parseInt(product.getRating()));*/
                        add(new Double[]{Double.parseDouble(product.getRating()), similarUserSimilarityMap.get(similarUserId)});
                    }});
                    helperProductIdMap.put(product.getId(), product);
                }
            })
        );

        Map<Product, Double> productsSuggestedRatingMap = new LinkedHashMap<>();

        recommendableProducts.forEach((productId, ratingsList) -> {
            if(ratingsList.size() >= minNumberOfSimilarUserRatings) { // suggest a product only if at least X similar people rated it
                /*double ratingsSum = 0.0;*/
                double numeratorSum = 0.0, denominatorSum = 0.0;

                for (Double[] pair : ratingsList) {
                    double rating = pair[0];
                    double similarity = pair[1];

                    /*ratingsSum += rating;*/
                    numeratorSum += rating * similarity;
                    denominatorSum += similarity;
                }
                /*double average = ratingsSum / ratingsList.size();*/
                double weightedAverage = numeratorSum / denominatorSum;

                productsSuggestedRatingMap.put(helperProductIdMap.get(productId), /*average*/ round(weightedAverage));
            }
        });

        Map<Product, Double> sortedProductsAverageRatingMap = sortProductsDescendingWithRating(productsSuggestedRatingMap);

        if(howMany != ALL_PRODUCTS_FLAG) {
            Map<Product, Double> filteredSortedProductsAverageRatingMap = new LinkedHashMap<>();
            int counter = 0;
            /*sortedProductsAverageRatingMap.forEach((product, rating) -> {*/
            for(Map.Entry<Product, Double> entry : sortedProductsAverageRatingMap.entrySet()) {
                if(counter <= howMany) {
                    counter++;
                    filteredSortedProductsAverageRatingMap.put(entry.getKey(), entry.getValue());
                } else break;
            }

            return filteredSortedProductsAverageRatingMap;
        }

        return sortedProductsAverageRatingMap;
    }

    public Map<Product, Double> getRecommendedProductsForUserRandom(Map<String, List<Product>> similarUserProductsMap, int howMany, int minNumberOfSimilarUserRatings) {

        // <productId, List<productRatings>>
        Map<Integer, List<Integer>> recommendableProducts = new HashMap<>();

        Map<Integer, Product> helperProductIdMap = new HashMap<>();

        similarUserProductsMap.forEach((similarUserId, productsList) ->
                        productsList.forEach(product -> {
                            if(recommendableProducts.containsKey(product.getId()))
                                recommendableProducts.get(product.getId()).add(Integer.parseInt(product.getRating()));
                            else {
                                recommendableProducts.put(product.getId(), new ArrayList<Integer>() {{
                                    add(Integer.parseInt(product.getRating()));
                                }});
                                helperProductIdMap.put(product.getId(), product);
                            }
                        })
        );

        Map<Product, Double> productsSuggestedRatingMap = new LinkedHashMap<>();

        recommendableProducts.forEach((productId, ratingsList) -> {
            if(ratingsList.size() >= minNumberOfSimilarUserRatings) { // suggest a product only if at least X similar people rated it
                double ratingsSum = 0.0;
                for (Integer rating : ratingsList) {
                    ratingsSum += rating;
                }
                double average = ratingsSum / ratingsList.size();

                productsSuggestedRatingMap.put(helperProductIdMap.get(productId), round(average));
            }
        });

        Map<Product, Double> sortedProductsAverageRatingMap = sortProductsDescendingWithRating(productsSuggestedRatingMap);

        if(howMany != ALL_PRODUCTS_FLAG) {
            Map<Product, Double> filteredSortedProductsAverageRatingMap = new LinkedHashMap<>();
            int counter = 0;
            /*sortedProductsAverageRatingMap.forEach((product, rating) -> {*/
            for(Map.Entry<Product, Double> entry : sortedProductsAverageRatingMap.entrySet()) {
                if(counter <= howMany) {
                    counter++;
                    filteredSortedProductsAverageRatingMap.put(entry.getKey(), entry.getValue());
                } else break;
            }

            return filteredSortedProductsAverageRatingMap;
        }

        return sortedProductsAverageRatingMap;
    }

    /**
     * Rounds the number to the nearest integer.
     * @param var
     * @return
     */
    public double round(double var) {
        BigDecimal bd = new BigDecimal(var);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Returns a list of highest rated products by a user.
     * @param userId Id of the user.
     * @param ratingLowest The returned products will have the user rating greater or equal <code>ratingLowest</code>.
     * @return List of highest rated products by the user.
     */
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

/*    @Deprecated
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
    }*/

/*    public static <K, V extends Comparable<? super V>> Map<K, V> sortProductsDescendingWithRating( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Comparator.comparing(e -> Integer.parseInt(e.getValue().toString()))))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }*/

    public static <K, V extends Comparable<? super V>> Map<K, V> sortProductsDescendingWithRating( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Comparator.comparing(e -> e.getValue())))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

}
