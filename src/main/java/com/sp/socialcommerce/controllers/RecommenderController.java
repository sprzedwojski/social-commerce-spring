package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.recommender.*;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Autowired
    private PredictiveAccuracyValidator predictiveAccuracyValidator;
    @Autowired
    private ClassificationAccuracyValidator classificationAccuracyValidator;

    private RecommendationValidator recommendationValidator;

    @Qualifier("graphDBManager")
    @Autowired
    protected GraphDBManager GDBM;


    @RequestMapping(method = RequestMethod.GET)
    public String recommenderPage() {
        return "recommend/home";
    }

/*    @RequestMapping(method = RequestMethod.POST, value = "/predictive/all_combinations")
    public String predictiveAllCombinations() {



        return "recommend/similar";
    }*/

    @RequestMapping(method = RequestMethod.POST, value = "/all_classification")
    public String testAllClassification(@RequestParam(value = "lowest_rating") String lowestRating,
                          @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                          @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings,
                          @RequestParam(value = "random_users", required = false, defaultValue = "false") String randomUsers) {

        Map<String, Double> classificationAccuracyMap = classificationAccuracyValidator.validate(lowestRating, numOfSimilarUsers, minSimUsersRatings/*, randomUsers*/);

        Double avgPrecision=null, avgRecall=null, avgFMeasure=null;
        if(classificationAccuracyMap.containsKey(ClassificationAccuracyValidator.PRECISION)) {
            avgPrecision = classificationAccuracyMap.get(ClassificationAccuracyValidator.PRECISION);
        }
        if(classificationAccuracyMap.containsKey(ClassificationAccuracyValidator.RECALL)) {
            avgRecall = classificationAccuracyMap.get(ClassificationAccuracyValidator.RECALL);
        }
        if(classificationAccuracyMap.containsKey(ClassificationAccuracyValidator.FMEASURE)) {
            avgFMeasure = classificationAccuracyMap.get(ClassificationAccuracyValidator.FMEASURE);
        }
        logger.info("Avg precision: " + avgPrecision + " | Avg recall: " + avgRecall + " | Avg fmeasure: " + avgFMeasure);

        return "recommend/similar";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all")
    public String testAll(@RequestParam(value = "lowest_rating") String lowestRating,
                          @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                          @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings,
                          @RequestParam(value = "random_users", required = false, defaultValue = "false") String randomUsers) {

        productRecommender.setK(Integer.parseInt(numOfSimilarUsers));
        productRecommender.setLowestRating(Integer.parseInt(lowestRating));
        productRecommender.setMinNumberOfSimilarUserRatings(Integer.parseInt(minSimUsersRatings));

        Map<String, Double> predictiveAccuracyMap = predictiveAccuracyValidator.validate(/*lowestRating, numOfSimilarUsers, minSimUsersRatings, randomUsers*/);

        Double avgRmse=null, avgMae=null;
        if(predictiveAccuracyMap.containsKey(PredictiveAccuracyValidator.RMSE)) {
            avgRmse = predictiveAccuracyMap.get(PredictiveAccuracyValidator.RMSE);
        }
        if(predictiveAccuracyMap.containsKey(PredictiveAccuracyValidator.MAE)) {
            avgMae = predictiveAccuracyMap.get(PredictiveAccuracyValidator.MAE);
        }
        logger.info("Avg RMSE: " + avgRmse + " | Avg MAE: " + avgMae);

        /*List<String> userIds = GDBM.getAllUsers();

        List<Double> correctnessList = new ArrayList<>();
        List<Double> rmseList = new ArrayList<>();
        List<Double> maeList = new ArrayList<>();

        final long startTime = System.currentTimeMillis();

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
                *//*highestRatedUserProducts.forEach(prod -> logger.info("> prod: " + prod.getNameEn() + " | rating: " + prod.getRating()));*//*

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
                *//*Double individualCorrectness = null;*//*
                if(realRatings.containsKey(prod.getId())) {
                    realRating = realRatings.get(prod.getId());
                    *//*individualCorrectness = *//*recommendationValidator.addRatings(Double.parseDouble(realRating), r);
                }
            });
            double correctness = recommendationValidator.getRecommendationCorrectness();
            double rmse = recommendationValidator.getRMSE();
            double mae = recommendationValidator.getMAE();

            if(!Double.isNaN(correctness) && !Double.isNaN(rmse) && !Double.isNaN(mae)) {
                correctnessList.add(correctness);
                rmseList.add(rmse);
                maeList.add(mae);
            }

            logger.info("Correctness: " + correctness + " | RSME: " + rmse + " | MAE: " + mae);
        });

        double avgCorrectness = RecommendationValidator.calculateListAverage(correctnessList);
        double avgRmse = RecommendationValidator.calculateListAverage(rmseList);
        double avgMae = RecommendationValidator.calculateListAverage(maeList);

        logger.info("Avg correctness: " + avgCorrectness + " | Avg RMSE: " + avgRmse + " | Avg MAE: " + avgMae);

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + " ms");*/

        return "recommend/similar";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String testSingleUser(@RequestParam(value = "user_id") String userId,
                                  @RequestParam(value = "lowest_rating") String lowestRating,
                                  @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                                  @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings) {

        testUser(userId, lowestRating, numOfSimilarUsers, minSimUsersRatings);


        return "recommend/similar";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users/all")
    public String testAllConfigurations() {

        String lowestRating = "4";
        List<Map<String, Double>> avgAccuracyMapsList = new ArrayList<>();

        for(int i=1; i<=10; i++) {
            for(int j=1; j<=10; j++) {
                if (j > i)
                    break;

                String K = Integer.toString(i);
                String minSimUsersRatings = Integer.toString(j);

                List<String> users = GDBM.getAllUsers();
                List<Map<String, Double>> accuracyMapsList = new ArrayList<>();

                users.forEach(userId -> accuracyMapsList.add(testUser(userId, lowestRating, K, minSimUsersRatings)));

                double totalCorrectness = 0.0, totalRMSE = 0.0, totalMAE = 0.0, totalRecall = 0.0, totalPrecision = 0.0, totalFMeasure = 0.0;
                int size = accuracyMapsList.size();
                for(Map<String, Double> accuracyMap : accuracyMapsList) {
/*                    totalCorrectness += accuracyMap.get("correctness");
                    totalRMSE += accuracyMap.get("rmse");
                    totalMAE += accuracyMap.get("mae");
                    totalRecall += accuracyMap.get("recall");
                    totalPrecision += accuracyMap.get("precision");
                    totalFMeasure += accuracyMap.get("fmeasure");*/

                    double res = accuracyMap.get("correctness") / size;
                    totalCorrectness += Double.isNaN(res) ? 0.0 : res;

                    res = accuracyMap.get("rmse") / size;
                    totalRMSE += Double.isNaN(res) ? 0.0 : res;

                    res = accuracyMap.get("mae") / size;
                    totalMAE += Double.isNaN(res) ? 0.0 : res;

                    res = accuracyMap.get("recall") / size;
                    totalRecall += Double.isNaN(res) ? 0.0 : res;

                    res = accuracyMap.get("precision") / size;
                    totalPrecision += Double.isNaN(res) ? 0.0 : res;

                    res = accuracyMap.get("fmeasure") / size;
                    totalFMeasure += Double.isNaN(res) ? 0.0 : res;
                }

                Map<String, Double> avgAccuracyMap = new HashMap<>();

/*                avgAccuracyMap.put("correctness", totalCorrectness / size);
                avgAccuracyMap.put("rmse", totalRMSE / size);
                avgAccuracyMap.put("mae", totalMAE / size);
                avgAccuracyMap.put("recall", totalRecall / size);
                avgAccuracyMap.put("precision", totalPrecision / size);
                avgAccuracyMap.put("fmeasure", totalFMeasure / size);
                avgAccuracyMap.put("k", Double.parseDouble(K));
                avgAccuracyMap.put("minSimUsersRatings", Double.parseDouble(minSimUsersRatings));*/

                avgAccuracyMap.put("correctness", totalCorrectness);
                avgAccuracyMap.put("rmse", totalRMSE);
                avgAccuracyMap.put("mae", totalMAE);
                avgAccuracyMap.put("recall", totalRecall);
                avgAccuracyMap.put("precision", totalPrecision);
                avgAccuracyMap.put("fmeasure", totalFMeasure);
                avgAccuracyMap.put("k", Double.parseDouble(K));
                avgAccuracyMap.put("minSimUsersRatings", Double.parseDouble(minSimUsersRatings));

                avgAccuracyMapsList.add(avgAccuracyMap);
            }
        }

        try {
            FileWriter writer = new FileWriter("/home/szymon/MASTER/results/result.csv");

            writer.append("k");writer.append(';');
            writer.append("minSimUsersRatings");writer.append(';');
            writer.append("correctness");writer.append(';');
            writer.append("rmse");writer.append(';');
            writer.append("mae");writer.append(';');
            writer.append("recall");writer.append(';');
            writer.append("precision");writer.append(';');
            writer.append("fmeasure");
            writer.append('\n');

            for(Map<String,Double> accuracyMap : avgAccuracyMapsList) {
                writer.append(Double.toString(accuracyMap.get("k")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("minSimUsersRatings")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("correctness")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("rmse")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("mae")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("recall")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("precision")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("fmeasure")));
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "recommend/similar";
    }


    public Map<String, Double> testUser(String userId, String lowestRating, String numOfSimilarUsers, String minSimUsersRatings) {
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

        // rated
        products.forEach((prod, r) -> {
            String realRating = null;
            Double individualCorrectness = null;
            if(realRatings.containsKey(prod.getId())) {
                realRating = realRatings.get(prod.getId());
                individualCorrectness = recommendationValidator.addRatings(Double.parseDouble(realRating), r);
                logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r + " >> real rating: " + realRating + " \tindividualCorrectness: " + individualCorrectness);
            }
        });

        // not rated
        logger.info("\n\nNOT RATED BY THE USER:");
        products.forEach((prod, r) -> {
            String realRating = null;
            Double individualCorrectness = null;
            if (!realRatings.containsKey(prod.getId())) {
                logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r);
            }
        });

        double correctness = recommendationValidator.getRecommendationCorrectness();
        double rmse = recommendationValidator.getRMSE();
        double mae = recommendationValidator.getMAE();

        logger.info("\n--");
        logger.info("Correctness: " + correctness);
        logger.info("RMSE: " + rmse);
        logger.info("MAE: " + mae);






        // Classification
        AtomicInteger relevantProducts = new AtomicInteger();
        products.forEach((prod, r) -> {
            double realRating;
            if(realRatings.containsKey(prod.getId())) {
                realRating = Double.parseDouble(realRatings.get(prod.getId()));
                if(realRating >= ClassificationAccuracyValidator.RELEVANCY_THRESHOLD)
                    relevantProducts.incrementAndGet();
            }
        });

        AtomicInteger allRelevantProducts = new AtomicInteger();
        realRatings.forEach((prodId, ratingString) -> {
            double rating = Double.parseDouble(ratingString);
            if(rating >= ClassificationAccuracyValidator.RELEVANCY_THRESHOLD)
                allRelevantProducts.incrementAndGet();
        });

        logger.info("Relevant recommended: " + relevantProducts.intValue());
        logger.info("All recommended: " + products.size());
        logger.info("All relevant: " + allRelevantProducts.intValue());

        double precision = ClassificationAccuracyValidator.calculatePrecision(relevantProducts.intValue(), products.size());
        double recall = ClassificationAccuracyValidator.calculateRecall(relevantProducts.intValue(), allRelevantProducts.intValue());
        double fmeasure = ClassificationAccuracyValidator.calculateFMeasure(precision, recall);

        logger.info("prec: " + precision + " | rec: " + recall + " | fm: " + fmeasure);


        Map<String, Double> accuracyMap = new HashMap<>();
        accuracyMap.put("correctness", correctness);
        accuracyMap.put("rmse", rmse);
        accuracyMap.put("mae", mae);
        accuracyMap.put("precision", precision);
        accuracyMap.put("recall", recall);
        accuracyMap.put("fmeasure", fmeasure);

        return accuracyMap;
    }
}
