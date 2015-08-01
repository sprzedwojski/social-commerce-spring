package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.labels.Product;
import com.sp.socialcommerce.neo4j.ProductRatingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/survey")
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private ProductRatingsService productRatingsService;

    @RequestMapping(value="/rate", method = RequestMethod.POST)
    public String rate(HttpServletRequest request) {
        String uid = (String) request.getSession().getAttribute("uid");
        String productId = request.getParameter("prod_id");
        String score = request.getParameter("score");
        String category = request.getParameter("category");
        logger.info("uid: " + uid + " | productId: " + productId + " | score: " + score + " | category: " + category);

        setSessionRating(request, category, productId, score);
        productRatingsService.setProductRating(uid, productId, score);

        return "survey/rate";
    }

    /**
     * Method setting the rating of a product in the session object productMap.
     */
    private void setSessionRating(HttpServletRequest request, String category, String productId, String score) {
        Map<String, List<Product>> productMap = (Map<String, List<Product>>) request.getSession().getAttribute("productMap");
        if(productMap != null) {
            List<Product> list = (List)productMap.get(category);
            if(list != null) {
                for(Product product : list) {
                    if(Integer.toString(product.getId()).equals(productId)) {
                        product.setRating(score);
                        logger.info("Session rating stored for productId " + productId + " | score: " + score);
                    }
                }
            } else {
                logger.error("Can't set session rating. List of products in category " + category + " is null.");
            }
        } else {
            logger.error("Can't set session rating. ProductMap is null.");
        }
    }

//    @RequestMapping(method = RequestMethod.POST)
//    public void nextCategory(HttpServletRequest request) {
//
//    }

    @RequestMapping
    public String surveyPage(ModelMap modelMap, HttpServletRequest request, @RequestParam(value = "current_category", required = false) String currentCategory) {

        if(RequestMethod.GET.toString().equals(request.getMethod())) {
            logger.info("surveyPage GET");

            // If the user is not logged in, we do not allow him to see the survey
            if (request.getSession().getAttribute("uid") == null) {
                return "redirect:login";
            }

            // If the user didn't select the categories, we redirect him to the intro page
            if (request.getSession().getAttribute("categories") == null) {
                return "redirect:survey_intro";
            }

        } else if(RequestMethod.POST.toString().equals(request.getMethod())) {
            logger.info("surveyPage POST");

            // TODO
        }

        String[] categories = (String[]) request.getSession().getAttribute("categories");
        Map<String, List<Product>> productMap;

        if (request.getSession().getAttribute("productMap") == null) {
            productMap = productRatingsService.getProductsByCategories(
                    request.getSession().getAttribute("uid").toString(), categories);
            request.getSession().setAttribute("productMap", productMap);
        } else {
            productMap = (Map<String, List<Product>>) request.getSession().getAttribute("productMap");
        }


        logger.info("currentCategory: " + currentCategory);
        logger.info("categories: " + categories.toString());

        String nextCategory = categories[0];
        double progress = 0.0;
        boolean isLast = false;
        if(currentCategory != null) {
            for(int i=0; i<categories.length; i++) {
                if(categories[i].equals(currentCategory)) {
                    if(categories.length <= i+1) {
                        // FIXME redirect na strone zakonczenia
                        logger.info("redirect to survey_intro");
                        return "redirect:survey_end";
                    } else {
                        nextCategory = categories[i+1];
                        logger.info("i: " + i);
                        logger.info("categories.length: " + categories.length);
                        logger.info("(double)i/categories.length: " + (double)i/categories.length);
                        progress = (double)(i+1)/categories.length * 100;

                        if(i+2 == categories.length) {
                            isLast = true;
                        }
                    }
                    break;
                }
            }
        }

        logger.info("nextCategory: " + nextCategory);
        logger.info("progress: " + progress);
        logger.info("isLast: " + isLast);

        Map<String, List<Product>> filteredProductMap = filterCategory(productMap, nextCategory);

        modelMap.addAttribute("productMap", filteredProductMap);
        modelMap.addAttribute("progress", progress);
        modelMap.addAttribute("isLast", isLast);

        return "survey";
    }

    private Map<String, List<Product>> filterCategory(Map<String, List<Product>> productMap, String category) {
        Iterator it = productMap.entrySet().iterator();
        Map<String, List<Product>> filteredMap = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key = (String) pair.getKey();
            if (key.compareToIgnoreCase(category) == 0) {
                filteredMap.put(key, (List<Product>) pair.getValue());
                return filteredMap;
            }
        }
        return null;
    }
}
