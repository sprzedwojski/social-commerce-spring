package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.GraphConstants;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.recommender.ProductRecommender;
import com.sp.socialcommerce.recommender.SimilarUser;
import com.sp.socialcommerce.recommender.UserSimilarityProcessor;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Qualifier("graphDBManager")
    @Autowired
    protected GraphDBManager GDBM;


    @RequestMapping(method = RequestMethod.GET)
    public String recommenderPage() {
        return "recommend/home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String getSimilarUsers(@RequestParam(value = "user_id", required = false) String userId) {

        final long startTime = System.currentTimeMillis();
        List<SimilarUser> similarUserList = userSimilarityProcessor.findSimilarUsers(userId, NUM_OF_SIMILAR_USERS);
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time (user similarity): " + (endTime - startTime) + " ms");

        Map<String, List<Product>> similarUserProductsMap = new HashMap<>();
        
        similarUserList.forEach((x) -> {
            logger.info("id: " + x.getUserId() + " | name: " + GDBM.getUserName(x.getUserId()) + " | similarity: " + x.getSimilaritySum());
            /*List<Product> highestRatedUserProducts = productRecommender.getUserHighestRatedProductsSortedDescendigly(x.getUserId(), 30);*/
            List<Product> highestRatedUserProducts = productRecommender.getUserHighestRatedProducts(x.getUserId(), 1);
            highestRatedUserProducts.forEach(prod -> logger.info("> prod: " + prod.getNameEn() + " | rating: " + prod.getRating()));
            
            similarUserProductsMap.put(x.getUserId(), highestRatedUserProducts);
        });

        logger.info("\n\n ..:: RECOMMENDED ::..\n\n");

        Map<Integer, String> realRatings = GDBM.getUserProductRatings(userId);

        Map<Product, Double> products = productRecommender.getRecommendedProductsForUser(similarUserProductsMap, -1, MIN_SIMILAR_USERS_RATINGS);
        products.forEach((prod, r) -> {
            String realRating = null;
            if(realRatings.containsKey(prod.getId()))
                realRating = realRatings.get(prod.getId());

            logger.info("> prod: " + prod.getNameEn() + " | suggested rating: " + r + " >> real rating: " + realRating);
        });


        return "recommend/similar";
    }
}
