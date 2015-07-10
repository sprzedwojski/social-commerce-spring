
package com.sp.socialcommerce.gigya;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gigya.socialize.GSRequest;
import com.gigya.socialize.GSResponse;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.prop.ApplicationProperties;
import com.sp.socialcommerce.prop.PropertiesConstants;

/**
 * Class responsible for asynchronously connecting with Gigya.
 */
@Service
public class GigyaService {

	private static final Logger logger = LoggerFactory.getLogger(GigyaService.class);
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private GraphDBManager GDBM;
	
	@Async
	public void processUser(String UID) {
		getUserData(UID);
		getUserFriends(UID);
	}
	
	public void getUserData(String UID) {
		// Step 1 - Defining the request
		String method = "socialize.getUserInfo";

		String gigyaTestApiKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_API_KEY);
		String gigyaSecretKey = applicationProperties.getProperty(PropertiesConstants.GIGYA_SECRET_KEY);

		logger.info("Gigya api key: " + gigyaTestApiKey);
		logger.info("Gigya secret key: " + gigyaSecretKey);

		GSRequest request = new GSRequest(gigyaTestApiKey, gigyaSecretKey, method);

		// Step 2 - Adding parameters
		request.setParam("uid", UID);  // set the "uid" parameter to user's ID
		request.setParam("extraFields", "religion, politicalView, likes, languages, relationshipStatus," +
				"hometown, education, work,  favorites");
		/*address, industry, specialties, skills,*/

		request.setAPIDomain("eu1.gigya.com");

		// Step 3 - Sending the request
		GSResponse response = request.send();

		// Step 4 - handling the request's response.
		if(response.getErrorCode()==0)
		{   // SUCCESS! response status = OK   
		    logger.info("Success in getUserInfo operation.");
		    logger.info(response.toString());

			GDBM.processUserResponse(response);
		} 
		else 
		{  // Error
			logger.error("Error code: " + response.getErrorCode());
		    logger.error("Got error on getUserData: " + response.getLog());
		}
	}
	
	public void getUserFriends(String UID) {
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
