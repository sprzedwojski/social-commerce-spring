package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.recommender.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Controller
/*@RequestMapping(value = "/recommend")*/
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


    /*@RequestMapping(method = RequestMethod.GET)*/
    public String recommenderPage() {
        return "recommend/home";
    }

    /*@RequestMapping(method = RequestMethod.POST, value = "/all_classification")*/
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

    /*@RequestMapping(method = RequestMethod.POST, value = "/all")*/
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

        return "recommend/similar";
    }

    private List<Integer> numberOfRecommendedProducts;

    /*@RequestMapping(method = RequestMethod.POST, value = "/users/all")*/
    public String testAllConfigurations(@RequestParam(value = "predictive") String predictive,
                                        @RequestParam(value = "num_of_products") int numOfProducts,
                                        @RequestParam(value = "min_sim_users_ratings_min") int minUsersRatingsMin,
                                        @RequestParam(value = "min_sim_users_ratings_max") int minUsersRatingsMax,
                                        @RequestParam(value = "lowest_rating") String lowestRating,
                                        @RequestParam(value = "k_max") int k_max,
                                        @RequestParam(value = "k_min") int k_min,
                                        @RequestParam(value = "top") String top){

        boolean isPredictive = predictive.equalsIgnoreCase("y");
        boolean isTop = top.equalsIgnoreCase("y");
        logger.info("isPredictive: " + isPredictive);
        logger.info("numOfProducts: " + numOfProducts);
        logger.info("minUsersRatingsMin: " + minUsersRatingsMin);
        logger.info("minUsersRatingsMax: " + minUsersRatingsMax);
        logger.info("lowestRating: " + lowestRating);
        logger.info("k_max: " + k_max);
        logger.info("k_min: " + k_min);

        StringBuilder filenameData = new StringBuilder();
        filenameData.append("_prods=").append(numOfProducts)
                .append("_minUsrRat=").append(minUsersRatingsMin).append("-").append(minUsersRatingsMax)
                .append("_k=").append(k_min).append("-").append(k_max)
                .append("_lowRat=").append(lowestRating);

        logger.info("");
        logger.info("Calculating...");

        /*String lowestRating = "1";*/
        List<Map<String, Double>> avgAccuracyMapsList = new ArrayList<>();

        numberOfRecommendedProducts = new ArrayList<>();

        for(int i=k_min; i<=k_max; i++) {
            for(int j=minUsersRatingsMin; j<=minUsersRatingsMax; j++) {
                if (j > i)
                    break;

                String K = Integer.toString(i);
                String minSimUsersRatings = Integer.toString(j);

                List<String> users = GDBM.getAllUsers();
                List<Map<String, Double>> accuracyMapsList = new ArrayList<>();

                users.forEach(userId -> accuracyMapsList.add(
                        testUser(userId, lowestRating, K, minSimUsersRatings, numOfProducts, isPredictive, isTop)));

                double /*totalCorrectness = 0.0,*/ totalRMSE = 0.0, totalMAE = 0.0, totalRecall = 0.0, totalPrecision = 0.0, totalFMeasure = 0.0;
                int size = accuracyMapsList.size();
                for(Map<String, Double> accuracyMap : accuracyMapsList) {

                    /*double res = accuracyMap.get("correctness") / size;
                    totalCorrectness += Double.isNaN(res) ? 0.0 : res;*/

                    double res = 0.0;

                    if(isPredictive) {
                        res = accuracyMap.get("rmse") / size;
                        totalRMSE += Double.isNaN(res) ? 0.0 : res;

                        res = accuracyMap.get("mae") / size;
                        totalMAE += Double.isNaN(res) ? 0.0 : res;
                    } else {
                        res = accuracyMap.get("recall") / size;
                        totalRecall += Double.isNaN(res) ? 0.0 : res;

                        res = accuracyMap.get("precision") / size;
                        totalPrecision += Double.isNaN(res) ? 0.0 : res;

                        res = accuracyMap.get("fmeasure") / size;
                        totalFMeasure += Double.isNaN(res) ? 0.0 : res;
                    }
                }

                Map<String, Double> avgAccuracyMap = new HashMap<>();

                /*avgAccuracyMap.put("correctness", totalCorrectness);*/
                if(isPredictive) {
                    avgAccuracyMap.put("rmse", totalRMSE);
                    avgAccuracyMap.put("mae", totalMAE);
                } else {
                    avgAccuracyMap.put("recall", totalRecall);
                    avgAccuracyMap.put("precision", totalPrecision);
                    avgAccuracyMap.put("fmeasure", totalFMeasure);
                }
                avgAccuracyMap.put("k", Double.parseDouble(K));
                avgAccuracyMap.put("minSimUsersRatings", Double.parseDouble(minSimUsersRatings));
                avgAccuracyMap.put("productsAvg", numberOfRecommendedProducts.stream().mapToInt(Integer::intValue).average().getAsDouble());

                numberOfRecommendedProducts.clear();

                avgAccuracyMapsList.add(avgAccuracyMap);
            }
        }

        String filename = "";
        String topString = isTop ? "_TOP" : "";
        if(isPredictive) {
            filename = "/home/szymon/MASTER/results/2015-09-15/result_pred" + topString + filenameData.toString() + "_" + System.currentTimeMillis() + ".csv";
        } else {
            filename = "/home/szymon/MASTER/results/2015-09-15/result_clas" + topString + filenameData.toString() + "_" + System.currentTimeMillis() + ".csv";
        }

        try {
            FileWriter writer = new FileWriter(filename);

            writer.append("k");writer.append(';');
            writer.append("minSimUsersRatings");writer.append(';');
            /*writer.append("correctness");writer.append(';');*/
            if(isPredictive) {
                writer.append("rmse");
                writer.append(';');
                writer.append("mae");
            } else {
                writer.append("recall");
                writer.append(';');
                writer.append("precision");
                writer.append(';');
                writer.append("fmeasure");
                writer.append(';');
            }
            writer.append("productsAvg");
            writer.append('\n');

            for(Map<String,Double> accuracyMap : avgAccuracyMapsList) {
                writer.append(Double.toString(accuracyMap.get("k")));writer.append(';');
                writer.append(Double.toString(accuracyMap.get("minSimUsersRatings")));writer.append(';');
                if(isPredictive) {
                    /*writer.append(Double.toString(accuracyMap.get("correctness")));
                    writer.append(';');*/
                    writer.append(Double.toString(accuracyMap.get("rmse")));
                    writer.append(';');
                    writer.append(Double.toString(accuracyMap.get("mae")));
                    /*writer.append(';');*/
                } else {
                    writer.append(Double.toString(accuracyMap.get("recall")));
                    writer.append(';');
                    writer.append(Double.toString(accuracyMap.get("precision")));
                    writer.append(';');
                    writer.append(Double.toString(accuracyMap.get("fmeasure")));
                    writer.append(';');
                }
                writer.append(Double.toString(accuracyMap.get("productsAvg")));
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("ALL DONE.");

        return "recommend/similar";
    }


    public Map<String, Double> testUser(String userId, String lowestRating, String numOfSimilarUsers,
                                        String minSimUsersRatings, int numOfProducts, boolean isPredictive,
                                        boolean isTopProducts) {
        Map<Product, Double> products;

        if(!isTopProducts) {
//        final long startTime = System.currentTimeMillis();
            List<SimilarUser> similarUserList =
                    userSimilarityProcessor.findSimilarUsers(userId, StringUtils.isNotBlank(numOfSimilarUsers) ? Integer.parseInt(numOfSimilarUsers) : NUM_OF_SIMILAR_USERS);
//        final long endTime = System.currentTimeMillis();

//        System.out.println("Total execution time (user similarity): " + (endTime - startTime) + " ms");

            Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
            Map<String, Double> similarUserSimilarityMap = new HashMap<>();

            similarUserList.forEach((x) -> {
//            logger.info("id: " + x.getUserId() + " | name: " + GDBM.getUserName(x.getUserId()) + " | similarity: " + x.getSimilaritySum());
                List<Product> highestRatedUserProducts =
                        productRecommender.getUserHighestRatedProducts(x.getUserId(), StringUtils.isNotBlank(lowestRating) ? Integer.parseInt(lowestRating) : 1);
//            highestRatedUserProducts.forEach(prod -> logger.info("> prod: " + prod.getNameEn() + " | rating: " + prod.getRating()));

                similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
                similarUserSimilarityMap.put(x.getUserId(), x.getSimilaritySum());
            });
            products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, numOfProducts,
                    StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);
//        logger.info("\n\n ..:: RECOMMENDED ::..\n\n");
        } else {
            products = productRecommender.getTopRatedProducts(numOfProducts);
        }

        numberOfRecommendedProducts.add(products.size());

        Map<Integer, String> realRatings = GDBM.getUserProductRatings(userId);

        recommendationValidator = new RecommendationValidator();

        // rated
        products.forEach((prod, r) -> {
            String realRating = null;
//            Double individualCorrectness = null;
            if(realRatings.containsKey(prod.getId())) {
                realRating = realRatings.get(prod.getId());
                /*individualCorrectness = */recommendationValidator.addRatings(Double.parseDouble(realRating), r);
//                logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r + " >> real rating: " + realRating + " \tindividualCorrectness: " + individualCorrectness);
            }
        });

        // not rated
//        logger.info("\n\nNOT RATED BY THE USER:");
        /*products.forEach((prod, r) -> {
            String realRating = null;
            Double individualCorrectness = null;
            if (!realRatings.containsKey(prod.getId())) {
                logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r);
            }
        });*/


        Map<String, Double> accuracyMap = new HashMap<>();

        /*double correctness = recommendationValidator.getRecommendationCorrectness();*/
        if(isPredictive) {
            double rmse = recommendationValidator.getRMSE();
            double mae = recommendationValidator.getMAE();

            accuracyMap.put("rmse", rmse);
            accuracyMap.put("mae", mae);

            /*accuracyMap.put("correctness", correctness);*/
        }

        /*logger.info("\n--");
        logger.info("Correctness: " + correctness);
        logger.info("RMSE: " + rmse);
        logger.info("MAE: " + mae);
*/




        if(!isPredictive) {
            // Classification
            AtomicInteger relevantProducts = new AtomicInteger();
            products.forEach((prod, r) -> {
                double realRating;
                if (realRatings.containsKey(prod.getId())) {
                    realRating = Double.parseDouble(realRatings.get(prod.getId()));
                    if (realRating >= ClassificationAccuracyValidator.RELEVANCY_THRESHOLD)
                        relevantProducts.incrementAndGet();
                }
            });

            AtomicInteger allRelevantProducts = new AtomicInteger();
            realRatings.forEach((prodId, ratingString) -> {
                double rating = Double.parseDouble(ratingString);
                if (rating >= ClassificationAccuracyValidator.RELEVANCY_THRESHOLD)
                    allRelevantProducts.incrementAndGet();
            });

        /*logger.info("Relevant recommended: " + relevantProducts.intValue());
        logger.info("All recommended: " + products.size());
        logger.info("All relevant: " + allRelevantProducts.intValue());*/

            double precision = ClassificationAccuracyValidator.calculatePrecision(relevantProducts.intValue(), products.size());
            double recall = ClassificationAccuracyValidator.calculateRecall(relevantProducts.intValue(), allRelevantProducts.intValue());
            double fmeasure = ClassificationAccuracyValidator.calculateFMeasure(precision, recall);

//        logger.info("prec: " + precision + " | rec: " + recall + " | fm: " + fmeasure);


            accuracyMap.put("precision", precision);
            accuracyMap.put("recall", recall);
            accuracyMap.put("fmeasure", fmeasure);
        }

        return accuracyMap;
    }
}
