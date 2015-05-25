package com.sp.socialcommerce.controllers;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gigya.socialize.GSRequest;
import com.gigya.socialize.GSResponse;
import com.sp.socialcommerce.models.User;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.prop.ApplicationProperties;
import com.sp.socialcommerce.prop.PropertiesConstants;

/**
 * Handles requests for the application home page.
 */
@Controller
@EnableAsync
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ApplicationProperties applicationProperties;

	private GraphDBManager GDBM = new GraphDBManager();

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
//		logger.info("Welcome home! The client locale is {}.", locale);
		
//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
//		
//		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("user", new User() );
		
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model) {				
		model.addAttribute("user", user);		
		logger.info("The client UID is {}.", user.getUID());

		createUserIfNotExists(user.getUID());
		getUserData(user.getUID());
		getUserFriends(user.getUID());
		return "redirect:survey";
	}
	
	@RequestMapping(value = "/getUserData", method = RequestMethod.POST)
	public String getUserData(@ModelAttribute User user, Locale locale, Model model) {				
		model.addAttribute("user", user);		
		logger.info(">> Inside getUserData");

//		GDBM = new GraphDBManager();
//		createUserIfNotExists(user.getUID());
		getUserData(user.getUID());
		getUserFriends(user.getUID());
		return "redirect:survey";
	}
	
	@RequestMapping(value = "/survey", method = RequestMethod.GET)
	public String surveyPage() {
		return "survey";
	}
	
	private void createUserIfNotExists(String UID) {
		GDBM.getUserNode(UID);
	}
	
	@Async
	private void getUserData(String UID) {
		// Step 1 - Defining the request
		String method = "socialize.getUserInfo";

		String gigyaTestApiKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_API_KEY);
		String gigyaSecretKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_SECRET_KEY);

		logger.info("Gigya api key: " + gigyaTestApiKey);
		logger.info("Gigya secret key: " + gigyaSecretKey);

		GSRequest request = new GSRequest(gigyaTestApiKey, gigyaSecretKey, method);

		// Step 2 - Adding parameters
		request.setParam("uid", UID);  // set the "uid" parameter to user's ID
		request.setParam("extraFields", "religion, politicalView, likes");

		request.setAPIDomain("eu1.gigya.com");

		// Step 3 - Sending the request
		GSResponse response = request.send();

		// Step 4 - handling the request's response.
		if(response.getErrorCode()==0)
		{   // SUCCESS! response status = OK   
		    logger.info("Success in getUserInfo operation.");
		    logger.info(response.toString());



//			GDBM.createUserNodeIfNotExists(response);
			GDBM.processUserResponse(response);
		} 
		else 
		{  // Error
			logger.error("Error code: " + response.getErrorCode());
		    logger.error("Got error on getUserData: " + response.getLog());
		}
		
//		saveUserData(null);
	}

	@Async
	private void getUserFriends(String UID) {
		String method = "socialize.getFriendsInfo";

		String gigyaTestApiKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_API_KEY);
		String gigyaSecretKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_SECRET_KEY);

		GSRequest request = new GSRequest(gigyaTestApiKey, gigyaSecretKey, method);

		// Step 2 - Adding parameters
		request.setParam("uid", UID);  // set the "uid" parameter to user's ID
//		request.setParam("extraFields", "industry, specialties, work, favorites");

		request.setAPIDomain("eu1.gigya.com");

		// Step 3 - Sending the request
		GSResponse response = request.send();

		// Step 4 - handling the request's response.
		if(response.getErrorCode() == 0) {   // SUCCESS! response status = OK
			logger.info("Success in getFriendsInfo operation.");
			logger.info(response.toString());
			
			GDBM.processUserFriendsResponse(UID, response);
		}
		else
		{  // Error
			System.out.println("Got error on getFriendsInfo: " + response.getLog());
		}
	}
	
}