package com.sp.socialcommerce.controllers;

import com.gigya.socialize.GSObject;
import com.sp.socialcommerce.gigya.GigyaService;
import com.sp.socialcommerce.models.User;
import com.sp.socialcommerce.prop.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Handles requests for the application home page.
 */
@Controller
@EnableAsync
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private GigyaService gigyaService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(Locale locale, Model model, HttpServletRequest request) {

		// If user id is present in the session we will not log him in again, but redirect directly to the survey
		if(request.getSession().getAttribute("uid") != null) {
			return "redirect:survey_intro";
		}

		model.addAttribute("user", new User() );
		model.addAttribute("sitename", Properties.GIGYA_SITENAME);
		model.addAttribute("apikey", Properties.GIGYA_API_KEY);

		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model, HttpServletRequest request) {
		model.addAttribute("user", user);		
		logger.info("The client UID is {}.", user.getUID());

		request.getSession().setAttribute("uid", user.getUID());

		try {
			logger.info("JavaScript Response:\n " + ((new GSObject(user.getUserInfo())).toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		gigyaService.processUser(user.getUID());
		return "redirect:survey_intro";
	}

	@RequestMapping(value = "/login/test", method = RequestMethod.POST)
	public void testUser(String uid){
		gigyaService.processUser(uid);
	}
	
}