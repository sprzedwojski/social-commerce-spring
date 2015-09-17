package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.facebook.FacebookService;
import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.recommender.ProductRecommender;
import com.sp.socialcommerce.recommender.UserSimilarityProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Controller
@RequestMapping(value = "/")
public class HomeController {

    @Autowired
    ProductRecommender productRecommender;
    @Autowired
    UserSimilarityProcessor userSimilarityProcessor;
    @Autowired
    FacebookService facebookService;

    @RequestMapping(method = RequestMethod.GET)
    public String home(HttpServletRequest request) {

        // If the user is not logged in, we redirect him to the login page
        if(request.getSession().getAttribute(FacebookService.USER_ID) == null
                || request.getSession().getAttribute(FacebookService.USER_ACCESS_TOKEN) == null) {
            return "redirect:login";
        }

        return "main";
    }

    @RequestMapping(value = "recommendations", method = RequestMethod.GET)
    public String getRecommendations(ModelMap modelMap, HttpServletRequest request) throws InterruptedException {

        String userId = (String) request.getSession().getAttribute(FacebookService.USER_ID);

        boolean isProcessingUser = facebookService.isProcessingUser(userId);
        while (isProcessingUser){
            System.out.println("\n\n\n\n PROCESSING USER. WAITING WITH RECOMMENDATIONS. \n\n\n\n");
            Thread.sleep(500);
            isProcessingUser = facebookService.isProcessingUser(userId);
        }

        productRecommender.setLowestRating(4);
        productRecommender.setK(7);
        productRecommender.setMinNumberOfSimilarUserRatings(4);

        Map<Product, Double> productsMap = productRecommender.getRecommendedProductsForUser(userId/*, K, MIN_SIMILAR_USERS_RATINGS, LOWEST_RATING*/);
        modelMap.addAttribute("productMap", productsMap);

        return "recommendations";
    }

    @RequestMapping(value = "recommendations/manual", method = RequestMethod.GET)
    public String getRecommendationsManual(ModelMap modelMap, HttpServletRequest request,
                                           @RequestParam(value = "lowest_rating") String lowestRating,
                                           @RequestParam(value = "num_of_similar_users") String numOfSimilarUsers,
                                           @RequestParam(value = "min_sim_users_ratings") String minSimUsersRatings) throws InterruptedException {

        String userId = (String) request.getSession().getAttribute(FacebookService.USER_ID);

        boolean isProcessingUser = facebookService.isProcessingUser(userId);
        while (isProcessingUser){
            System.out.println("\n\n\n\n PROCESSING USER. WAITING WITH RECOMMENDATIONS. \n\n\n\n");
            Thread.sleep(500);
            isProcessingUser = facebookService.isProcessingUser(userId);
        }

        productRecommender.setLowestRating(Integer.parseInt(lowestRating));
        productRecommender.setK(Integer.parseInt(numOfSimilarUsers));
        productRecommender.setMinNumberOfSimilarUserRatings(Integer.parseInt(minSimUsersRatings));

        Map<Product, Double> productsMap = productRecommender.getRecommendedProductsForUser(userId/*, K, MIN_SIMILAR_USERS_RATINGS, LOWEST_RATING*/);
        modelMap.addAttribute("productMap", productsMap);

        return "recommendations";
    }

}
