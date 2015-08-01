package com.sp.socialcommerce.controllers;

import com.sp.socialcommerce.facebook.FacebookService;
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

    @RequestMapping(method = RequestMethod.GET)
    public String surveyPage(ModelMap modelMap, HttpServletRequest request) {

        // If the user is not logged in, we do not allow him to see the survey
        if(request.getSession().getAttribute(FacebookService.USER_ID) == null
                || request.getSession().getAttribute(FacebookService.USER_ACCESS_TOKEN) == null) {
            return "redirect:login";
        }

        modelMap.addAttribute("progress", 100);

        return "survey_end";
    }
}
