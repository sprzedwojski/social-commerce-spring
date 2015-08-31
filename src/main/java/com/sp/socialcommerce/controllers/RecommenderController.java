package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.recommender.ProductRecommender;
import com.sp.socialcommerce.recommender.RecommendationValidator;
import com.sp.socialcommerce.recommender.SimilarUser;
import com.sp.socialcommerce.recommender.UserSimilarityProcessor;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Controller
@RequestMapping(value = "/recommend")
public class RecommenderController {

    private static final Logger logger = LoggerFactory.getLogger(RecommenderController.class);

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


    @RequestMapping(method = RequestMethod.GET)
    public String recommenderPage() {
        return "recommend/home";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all")
    public String testAll(@RequestParam(value = "lowest_rating") String lowestRating,
                          @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                          @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings,
                          @RequestParam(value = "random_users", required = false, defaultValue = "false") String randomUsers) {
        List<String> userIds = GDBM.getAllUsers();

        List<Double> correctnessList = new ArrayList<>();
        List<Double> rmseList = new ArrayList<>();

        final long startTime = System.currentTimeMillis();

        userIds.forEach(userId -> {
            List<SimilarUser> similarUserList = null;

            if(Boolean.parseBoolean(randomUsers) == false)
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

            if(Boolean.parseBoolean(randomUsers) == false)
                products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, -1,
                        StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);
            else
                products = productRecommender.getRecommendedProductsForUserRandom(similarUserProductsMap, -1,
                        StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);

            products.forEach((prod, r) -> {
                String realRating = null;
                /*Double individualCorrectness = null;*/
                if(realRatings.containsKey(prod.getId())) {
                    realRating = realRatings.get(prod.getId());
                    /*individualCorrectness = */recommendationValidator.addRatings(Double.parseDouble(realRating), r);
                }
            });
            double correctness = recommendationValidator.getRecommendationCorrectness();
            double rmse = recommendationValidator.getRMSE();

            if(!Double.isNaN(correctness) && !Double.isNaN(rmse)) {
                correctnessList.add(correctness);
                rmseList.add(rmse);
            }

            logger.info("Correctness: " + correctness + " | RSME: " + rmse);
        });

        double avgCorrectness = RecommendationValidator.calculateListAverage(correctnessList);
        double avgRmse = RecommendationValidator.calculateListAverage(rmseList);

        logger.info("Avg correctness: " + avgCorrectness + " | Avg RMSE: " + avgRmse);

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + " ms");

        return "recommend/similar";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String getSimilarUsers(@RequestParam(value = "user_id") String userId,
                                  @RequestParam(value = "lowest_rating") String lowestRating,
                                  @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                                  @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings) {

        final long startTime = System.currentTimeMillis();
        List<SimilarUser> similarUserList =
                userSimilarityProcessor.findSimilarUsers(userId, StringUtils.isNotBlank(numOfSimilarUsers) ? Integer.parseInt(numOfSimilarUsers) : NUM_OF_SIMILAR_USERS);
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time (user similarity): " + (endTime - startTime) + " ms");

        Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
        Map<String, Double> similarUserSimilarityMap = new HashMap<>();
        
        similarUserList.forEach((x) -> {
            logger.info("id: " + x.getUserId() + " | name: " + GDBM.getUserName(x.getUserId()) + " | similarity: " + x.getSimilaritySum());
            List<Product> highestRatedUserProducts =
                    productRecommender.getUserHighestRatedProducts(x.getUserId(), StringUtils.isNotBlank(lowestRating) ? Integer.parseInt(lowestRating) : 1);
            highestRatedUserProducts.forEach(prod -> logger.info("> prod: " + prod.getNameEn() + " | rating: " + prod.getRating()));
            
            similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
            similarUserSimilarityMap.put(x.getUserId(), x.getSimilaritySum());
        });

        logger.info("\n\n ..:: RECOMMENDED ::..\n\n");

        Map<Integer, String> realRatings = GDBM.getUserProductRatings(userId);

        recommendationValidator = new RecommendationValidator();

        Map<Product, Double> products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, -1,
                StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);
        products.forEach((prod, r) -> {
            String realRating = null;
            Double individualCorrectness = null;
            if(realRatings.containsKey(prod.getId())) {
                realRating = realRatings.get(prod.getId());
                individualCorrectness = recommendationValidator.addRatings(Double.parseDouble(realRating), r);
            }

            logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r + " >> real rating: " + realRating + " \tindividualCorrectness: " + individualCorrectness);
        });
        logger.info("\n--");
        logger.info("Correctness: " + recommendationValidator.getRecommendationCorrectness());


        return "recommend/similar";
    }
}
