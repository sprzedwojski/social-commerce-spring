package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.facebook.FacebookService;
import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.recommender.ProductRecommender;
import com.sp.socialcommerce.recommender.SimilarUser;
import com.sp.socialcommerce.recommender.UserSimilarityProcessor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    private final int K = 3;
    private final int MIN_SIMILAR_USERS_RATINGS = 2;
    private final int LOWEST_RATING = 4;

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

        Map<Product, Double> productsMap = productRecommender.getRecommendedProductsForUser(userId, K, MIN_SIMILAR_USERS_RATINGS, LOWEST_RATING);
        modelMap.addAttribute("productMap", productsMap);

        return "recommendations";
    }

}
