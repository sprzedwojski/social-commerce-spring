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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Service
public class ClassificationAccuracyValidator {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationAccuracyValidator.class);

    public static final String PRECISION = "precision";
    public static final String RECALL = "recall";
    public static final String FMEASURE = "fmeasure";

    private static final int NUM_OF_SIMILAR_USERS = 10;
    private static final int MIN_SIMILAR_USERS_RATINGS = 4;

    private static final double RELEVANCY_THRESHOLD = 4.0;

    @Autowired
    private UserSimilarityProcessor userSimilarityProcessor;
    @Autowired
    private ProductRecommender productRecommender;
    private RecommendationValidator recommendationValidator;

    @Qualifier("graphDBManager")
    @Autowired
    protected GraphDBManager GDBM;

    public Map<String, Double> validate(String lowestRating, String numOfSimilarUsers, String minSimUsersRatings/*, String randomUsers*/) {

        Map<String, Double> classificationAccuracyMap = new HashMap<>();

        List<String> userIds = GDBM.getAllUsers();

        List<Double> precisionList = new ArrayList<>();
        List<Double> recallList = new ArrayList<>();
        List<Double> fMeasureList = new ArrayList<>();

        userIds.forEach(userId -> {
            List<SimilarUser> similarUserList = null;
            similarUserList = userSimilarityProcessor.findSimilarUsers(userId, StringUtils.isNotBlank(numOfSimilarUsers)
                        ? Integer.parseInt(numOfSimilarUsers) : NUM_OF_SIMILAR_USERS);

            Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
            Map<String, Double> similarUserSimilarityMap = new HashMap<>();

            similarUserList.forEach((x) -> {
                List<Product> highestRatedUserProducts =
                        productRecommender.getUserHighestRatedProducts(x.getUserId(), StringUtils.isNotBlank(lowestRating) ? Integer.parseInt(lowestRating) : 1);

                similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
                similarUserSimilarityMap.put(x.getUserId(), x.getSimilaritySum());
            });

            Map<Integer, String> realRatings = GDBM.getUserProductRatings(userId);

            recommendationValidator = new RecommendationValidator();

            Map<Product, Double> products = null;
            products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, similarUserSimilarityMap, -1,
                        StringUtils.isNotBlank(minSimUsersRatings) ? Integer.parseInt(minSimUsersRatings) : MIN_SIMILAR_USERS_RATINGS);


            /*List<Product> relevantProducts = new ArrayList<>();*/
            AtomicInteger relevantProducts = new AtomicInteger();

            products.forEach((prod, r) -> {
                double realRating;
                if(realRatings.containsKey(prod.getId())) {
                    realRating = Double.parseDouble(realRatings.get(prod.getId()));
                    if(realRating > RELEVANCY_THRESHOLD)
                        /*relevantProducts.add(prod);*/
                        relevantProducts.incrementAndGet();
                }
            });

            /*List<Product> allRelevantProducts = new ArrayList<>();*/
            AtomicInteger allRelevantProducts = new AtomicInteger();

            realRatings.forEach((prodId, ratingString) -> {
                double rating = Double.parseDouble(ratingString);
                if(rating > RELEVANCY_THRESHOLD)
                    allRelevantProducts.incrementAndGet();
            });


            logger.info("Relevant: " + relevantProducts.intValue());
            logger.info("All recommended: " + products.size());
            logger.info("All relevant: " + allRelevantProducts.intValue());

            double precision = calculatePrecision(relevantProducts.intValue(), products.size());
            double recall = calculateRecall(relevantProducts.intValue(), allRelevantProducts.intValue());
            double fmeasure = calculateFMeasure(precision, recall);

            if(Double.isNaN(precision))
                precision = 0.0;

            if(Double.isNaN(recall))
                recall = 0.0;

            if(Double.isNaN(fmeasure))
                fmeasure = 0.0;

            precisionList.add(precision);
            recallList.add(recall);
            fMeasureList.add(fmeasure);

            logger.info("prec: " + precision + " | rec: " + recall + " | fm: " + fmeasure);
        });

        double avgPrecision = RecommendationValidator.calculateListAverage(precisionList);
        double avgRecall = RecommendationValidator.calculateListAverage(recallList);
        double avgFMeasure = RecommendationValidator.calculateListAverage(fMeasureList);

        classificationAccuracyMap.put(PRECISION, avgPrecision);
        classificationAccuracyMap.put(RECALL, avgRecall);
        classificationAccuracyMap.put(FMEASURE, avgFMeasure);

        return classificationAccuracyMap;
    }

    private double calculatePrecision(int relevantItemsSelected, int allItemsSelected) {
        return (double) relevantItemsSelected / allItemsSelected;
    }

    private double calculateRecall(int relevantItemsSelected, int allRelevantItems) {
        return (double) relevantItemsSelected / allRelevantItems;
    }

    private double calculateFMeasure(double precision, double recall) {
        return 2 * precision * recall / (precision + recall);
    }

}
