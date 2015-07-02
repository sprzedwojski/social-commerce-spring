package com.sp.socialcommerce.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value = "/survey_end")
public class SurveyEndController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyEndController.class);

//    @Autowired
//    private ProductRatingsService productRatingsService;

    @RequestMapping(method = RequestMethod.GET)
    public String surveyPage(ModelMap modelMap, HttpServletRequest request) {

        // If the user is not logged in, we do not allow him to see the survey
        if(request.getSession().getAttribute("uid") == null) {
            return "redirect:login";
        }

        // TODO pobrac i przekazac opis ankiety dla uzytkownikow
        modelMap.addAttribute("jumboTitle", "Title");
        modelMap.addAttribute("jumboText", "Jumbo Text");

        modelMap.addAttribute("progress", 100);

        return "survey_end";
    }

//    @RequestMapping(method = RequestMethod.POST)
//    public String registerChoices(HttpServletRequest request, @RequestParam(value="categories[]") String... categories) {
//
//        logger.info("registerChoices");
//
//        for(String cat : categories) {
//            logger.info("category selected: " + cat);
//        }
//
//        // TODO zapisanie wyborów uzytkownika (w sesji? w bazie?)
//        request.getSession().setAttribute("categories", categories);
//
//        return "redirect:survey";
//    }
}
