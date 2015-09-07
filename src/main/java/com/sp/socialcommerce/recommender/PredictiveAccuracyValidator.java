package com.sp.socialcommerce.recommender;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Service
public class PredictiveAccuracyValidator {

    private static final Logger logger = LoggerFactory.getLogger(PredictiveAccuracyValidator.class);

    public static final String RMSE = "rmse";
    public static final String MAE = "mae";

    private static final int NUM_OF_SIMILAR_USERS = 10;
    private static final int MIN_SIMILAR_USERS_RATINGS = 4;

    @Autowired
    private UserSimilarityProcessor userSimilarityProcessor;
    @Autowired
    private ProductRecommender productRecommender;
    private RecommendationValidator recommendationValidator;

    @Qualifier("graphDBManager")
    @Autowired
    protected GraphDBManager GDBM;

    /**
     * Performs k-fold cross validation.
     *
     * @param lowestRating
     * @param numOfSimilarUsers
     * @param minSimUsersRatings
     * @param randomUsers
     * @return Map with cross validation results: key - type of result, value - the numerical result.
     */
    public Map<String, Double> validate(String lowestRating, String numOfSimilarUsers, String minSimUsersRatings, String randomUsers) {
        Map<String, Double> predictiveAccuracyMap = new HashMap<>();

        List<String> userIds = GDBM.getAllUsers();

        /*List<Double> correctnessList = new ArrayList<>();*/
        List<Double> rmseList = new ArrayList<>();
        List<Double> maeList = new ArrayList<>();

        /*final long startTime = System.currentTimeMillis();*/

        userIds.forEach(userId -> {
            List<SimilarUser> similarUserList = null;

            if(!Boolean.parseBoolean(randomUsers))
                similarUserList = userSimilarityProcessor.findSimilarUsers(userId, StringUtils.isNotBlank(numOfSimilarUsers)
                        ? Integer.parseInt(numOfSimilarUsers) : NUM_OF_SIMILAR_USERS);
            else
                similarUserList = userSimilarityProcessor.findRandomUsers(userId, StringUtils.isNotBlank(numOfSimilarUsers)
                        ? Integer.parseInt(numOfSimilarUsers) : NUM_OF_SIMILAR_USERS);

            Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
            Map<String, Double> similarUserSimilarityMap = new HashMap<>();

            similarUserList.forEach((x) -> {
                List<Product> highestRatedUserProducts =
                        productRecommender.getUserHighestRatedProducts(x.getUserId(), StringUtils.isNotBlank(lowestRating) ? Integer.parseInt(lowestRating) : 1);
                /*highestRatedUserProducts.forEach(prod -> logger.info("> prod: " + prod.getNameEn() + " | rating: " + prod.getRating()));*/

                similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
                similarUserSimilarityMap.put(x.getUserId(), x.getSimilaritySum());
            });

            Map<Integer, String> realRatings = GDBM.getUserProductRatings(userId);

            recommendationValidator = new RecommendationValidator();

            Map<Product, Double> products = null;

            if(!Boolean.parseBoolean(randomUsers))
                products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, -1,
                        StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);
            else
                products = productRecommender.getRecommendedProductsForUserRandom(similarUserProductsMap, -1,
                        StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);

            products.forEach((prod, r) -> {
                String realRating = null;
                if(realRatings.containsKey(prod.getId())) {
                    realRating = realRatings.get(prod.getId());
                    recommendationValidator.addRatings(Double.parseDouble(realRating), r);
                }
            });
            /*double correctness = recommendationValidator.getRecommendationCorrectness();*/
            double rmse = recommendationValidator.getRMSE();
            double mae = recommendationValidator.getMAE();

            if(/*!Double.isNaN(correctness) &&*/ !Double.isNaN(rmse) && !Double.isNaN(mae)) {
                /*correctnessList.add(correctness);*/
                rmseList.add(rmse);
                maeList.add(mae);
            }

            /*logger.info(*//*"Correctness: " + correctness + *//*"RMSE: " + rmse + " | MAE: " + mae);*/
        });

        /*double avgCorrectness = RecommendationValidator.calculateListAverage(correctnessList);*/
        double avgRmse = RecommendationValidator.calculateListAverage(rmseList);
        double avgMae = RecommendationValidator.calculateListAverage(maeList);

        /*logger.info(*//*"Avg correctness: " + avgCorrectness + *//*"Avg RMSE: " + avgRmse + " | Avg MAE: " + avgMae);*/

        predictiveAccuracyMap.put(RMSE, avgRmse);
        predictiveAccuracyMap.put(MAE, avgMae);

        return predictiveAccuracyMap;
    }

}
