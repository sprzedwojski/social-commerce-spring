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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/survey_intro")
public class SurveyIntroController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyIntroController.class);

    @Autowired
    private ProductRatingsService productRatingsService;

    @RequestMapping(method = RequestMethod.GET)
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

    @RequestMapping(method = RequestMethod.POST)
    public String registerChoices(@RequestParam(value="categories") String... categories) {

        logger.info("registerChoices");

        for(String cat : categories) {
            logger.info("category selected: " + cat);
        }

        // TODO zapisanie wyborów uzytkownika (w sesji? w bazie?)

        return "redirect:survey";
    }
}
