package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.gigya.ProductRatingsService;
import com.sp.socialcommerce.labels.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private ProductRatingsService productRatingsService;
//    @Autowired
//    private UserHolder user;

    @RequestMapping(value="/survey", method= RequestMethod.POST)
    public void rateProduct(HttpServletRequest request) {
        String uid = (String) request.getSession().getAttribute("uid");
        String productId = request.getParameter("prod_id");
        String score = request.getParameter("score");
        logger.info("uid: " + uid + " | productId: " + productId + " | score: " + score);

        productRatingsService.setProductRating(uid, productId, score);
    }

    @RequestMapping(value = "/survey", method = RequestMethod.GET)
    public String surveyPage(ModelMap modelMap, HttpServletRequest request) {

        // If the user is not logged in, we do not allow him to see the survey
        if(request.getSession().getAttribute("uid") == null) {
            return "redirect:login";
        }

//        List<Product> productList = productRatingsService.getProducts(request.getSession().getAttribute("uid").toString());
        Map<String, List<Product>> productMap = productRatingsService.getProductsByCategories(request.getSession().getAttribute("uid").toString());

//        modelMap.addAttribute("productList", productList);
        modelMap.addAttribute("productMap", productMap);

        // TODO pobrac i przekazac opis ankiety dla uzytkownikow
        modelMap.addAttribute("jumboTitle", "Title");
        modelMap.addAttribute("jumboText", "Jumbo Text");

        return "survey_intro";
    }

    @RequestMapping(value = "/survey/register-choices", method = RequestMethod.POST)
    public void registerChoices(HttpServletRequest request) {
        logger.info("registerChoices");
    }
}
