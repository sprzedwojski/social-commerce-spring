package com.sp.socialcommerce.controllers;

import java.util.Locale;
import java.util.Set;

import com.sp.socialcommerce.gigya.ProductRatingsService;
import com.sp.socialcommerce.labels.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sp.socialcommerce.gigya.GigyaService;
import com.sp.socialcommerce.models.User;
import com.sp.socialcommerce.prop.ApplicationProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles requests for the application home page.
 */
@Controller
@EnableAsync
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private GigyaService gigyaService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(Locale locale, Model model, HttpServletRequest request) {

		// If user id is present in the session we will not log him in again, but redirect directly to the survey
		if(request.getSession().getAttribute("uid") != null) {
			return "redirect:survey";
		}

		model.addAttribute("user", new User() );
		
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model, HttpServletRequest request) {
		model.addAttribute("user", user);		
		logger.info("The client UID is {}.", user.getUID());

		request.getSession().setAttribute("uid", user.getUID());

		gigyaService.processUser(user.getUID());
		return "redirect:survey";
	}
	
	@RequestMapping(value = "/getUserData", method = RequestMethod.POST)
	public String getUserData(@ModelAttribute User user, Locale locale, Model model) {
		model.addAttribute("user", user);		
		logger.info(">> Inside getUserData");

		gigyaService.getUserData(user.getUID());
		gigyaService.getUserFriends(user.getUID());
		return "redirect:survey";
	}
	
}