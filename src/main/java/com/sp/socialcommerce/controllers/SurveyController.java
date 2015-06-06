package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.gigya.ProductRatingsService;
import com.sp.socialcommerce.prop.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by szymon on 6/6/15.
 */
@Controller
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductRatingsService productRatingsService;
    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(value="/survey", method= RequestMethod.POST)
    public void rateProduct(HttpServletRequest request, HttpServletResponse response) {
        String uid = request.getParameter("uid");
        String productId = request.getParameter("prod_id");
        String score = request.getParameter("score");
        logger.info("uid: " + uid + " | productId: " + productId + " | score: " + score);
    }
}
