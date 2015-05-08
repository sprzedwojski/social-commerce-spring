package com.sp.socialcommerce.controllers;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sp.socialcommerce.models.User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
//		logger.info("Welcome home! The client locale is {}.", locale);
		
//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
//		
//		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("user", new User() );
		
		return "home";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model) {
		
//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
//		
//		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("user", user);
		
		logger.info("The client UID is {}.", user.getUID());
		logger.info("The client UID is " + user.getUID());
		
		return "home";
	}
	
	/*private void getUserData(String UID) {
		// Step 1 - Defining the request
		String method = "socialize.getUserInfo";
		GSRequest request = new GSRequest(gigyaTestApiKey, gigyaSecretKey, method);

		// Step 2 - Adding parameters
		request.setParam("uid", UID);  // set the "uid" parameter to user's ID
		request.setParam("extraFields", "likes");

		request.setAPIDomain("eu1.gigya.com");

		// Step 3 - Sending the request
		GSResponse response = request.send();

		// Step 4 - handling the request's response.
		if(response.getErrorCode()==0)
		{   // SUCCESS! response status = OK   
		    System.out.println("Success in setStatus operation.");
		    System.out.println(response);
		} 
		else 
		{  // Error
		    System.out.println("Got error on setStatus: " + response.getLog());
		}
		
		saveUserData(null);
	}*/
	
}