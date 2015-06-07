package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.gigya.ProductRatingsService;
import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.prop.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * Created by szymon on 6/6/15.
 */
@Controller
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductRatingsService productRatingsService;
//    @Autowired
//    private ApplicationProperties applicationProperties;

    @RequestMapping(value="/survey", method= RequestMethod.POST)
    public void rateProduct(HttpServletRequest request) {
        String uid = request.getParameter("uid");
        String productId = request.getParameter("prod_id");
        String score = request.getParameter("score");
        logger.info("uid: " + uid + " | productId: " + productId + " | score: " + score);
    }

    @RequestMapping(value = "/survey", method = RequestMethod.GET)
    public String surveyPage(ModelMap modelMap, HttpServletRequest request) {

        // If the user is not logged in, we do not allow him to see the survey
        if(request.getSession().getAttribute("uid") == null) {
            return "redirect:login";
        }

        List<Product> productList = productRatingsService.getProducts();

        modelMap.addAttribute("productList", productList);

        // TODO pobrac i przekazac opis ankiety dla uzytkownikow
        modelMap.addAttribute("jumboTitle", "Title");
        modelMap.addAttribute("jumboText", "Jumbo Text");

        return "survey";
    }
}
