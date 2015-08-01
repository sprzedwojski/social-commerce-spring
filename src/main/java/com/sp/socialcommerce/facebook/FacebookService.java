
package com.sp.socialcommerce.facebook;

import com.gigya.socialize.GSRequest;
import com.gigya.socialize.GSResponse;
import com.restfb.*;
import com.restfb.exception.*;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Likes;
import com.restfb.types.NamedFacebookType;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.prop.ApplicationProperties;
import com.sp.socialcommerce.prop.Properties;
import com.sp.socialcommerce.prop.PropertiesConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Class responsible for asynchronously connecting with Facebook.
 */
@Service
public class FacebookService {

	private static final Logger logger = LoggerFactory.getLogger(FacebookService.class);

	public static final String USER_ID = "userId";
	public static final String USER_ACCESS_TOKEN = "accessToken";

	public static final String MAP_USER_PROFILE = "userProfile";
	public static final String MAP_USER_LIKES = "userLikes";
	public static final String MAP_USER_FRIENDS = "userFriends";

	/*public static final String MAP_USER_AGE_RANGE = "userAgeRange";*/
	public static final String MAP_USER_POLITICAL = "userPolitical";
	public static final String MAP_USER_RELIGION = "userReligion";
	public static final String MAP_USER_HOMETOWN = "userHometown";
	public static final String MAP_USER_LOCATION = "userLocation";
	public static final String MAP_USER_GENDER = "userGender";
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private GraphDBManager GDBM;
	
	@Async
	public void processUser(String userId, String accessToken) {

		HashMap<String, Object> responseMap = new HashMap<>();

		try {
			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Properties.FB_APP_SECRET, Version.VERSION_2_4);

			// ==============================
			// USER PROFILE
			// ==============================

			com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class,
					Parameter.with("fields", "age_range,hometown,location,political,religion,relationship_status,gender,sports," +
							"favorite_athletes,favorite_teams"));
			responseMap.put(MAP_USER_PROFILE, fbUser);
			responseMap.put(MAP_USER_GENDER, fbUser.getGender());
			responseMap.put(MAP_USER_HOMETOWN, fbUser.getHometownName());
			responseMap.put(MAP_USER_LOCATION, fbUser.getLocation().getName());
			responseMap.put(MAP_USER_POLITICAL, fbUser.getPolitical());
			responseMap.put(MAP_USER_RELIGION, fbUser.getReligion());

			logger.info("User profile: " + fbUser.toString());
			/*logger.info("User name: " + fbUser.getName());
			logger.info("User political: " + fbUser.getPolitical());
			logger.info("User religion:" + fbUser.getReligion());*/




			// ==============================
			// LIKES
			// ==============================

			int counter = 0;
			String after = null;
			List<JsonArray> likesArrayList = new ArrayList<>();
			do {
				JsonObject jsonObject;
				if(after == null)
					jsonObject = facebookClient.fetchObject("me/likes", JsonObject.class,
							Parameter.with("fields", "id,name,category"));
				else
					jsonObject = facebookClient.fetchObject("me/likes", JsonObject.class,
							Parameter.with("fields", "id,name,category"), Parameter.with("after", after));

				after = null;
				if(jsonObject == null) {
					logger.error("JsonObject is null!");
					break;
				}
				logger.info(">> Likes: " + jsonObject.toString());

				JsonArray likesArray = jsonObject.getJsonArray("data");
				if(likesArray == null || likesArray.length() == 0) {
					logger.error("Likes array is null or empty!");
					break;
				}

				likesArrayList.add(likesArray);

				counter += likesArray.length();
				/*logger.info("likes array: " + likesArray.toString());
				logger.info("count total:" + counter);*/
				after = (String) jsonObject.getJsonObject("paging").getJsonObject("cursors").get("after");
			} while(StringUtils.isNotBlank(after));

			/*FIXME TEMP TEST*/
			responseMap.put(MAP_USER_LIKES, likesArrayList);



			// ==============================
			// FRIENDS
			// ==============================
			counter = 0;
			after = null;
			List<JsonArray> friendsArrayList = new ArrayList<>();
			do {
				JsonObject jsonObject;
				if(after == null)
					jsonObject = facebookClient.fetchObject("me/friends", JsonObject.class);
				else
					jsonObject = facebookClient.fetchObject("me/friends", JsonObject.class, Parameter.with("after", after));

				after = null;
				if(jsonObject == null) {
					logger.error("JsonObject is null!");
					break;
				}
				logger.info(">> Friends: " + jsonObject.toString());

				JsonArray friendsArray = jsonObject.getJsonArray("data");
				if(friendsArray == null || friendsArray.length() == 0) {
					logger.error("Friends array is null or empty!");
					break;
				}

				logger.info("Adding friendsArray to list...");
				likesArrayList.add(friendsArray);

				/*counter += friendsArray.length();*/
				/*logger.info("friends array: " + friendsArray.toString());
				logger.info("count total:" + counter);*/

				logger.info("Searching for 'after'...");
				if(jsonObject.getJsonObject("paging") != null)
					if(jsonObject.getJsonObject("cursors") != null)
						after = (String) jsonObject.getJsonObject("paging").getJsonObject("cursors").get("after");
				logger.info("After: " + after);

			} while(StringUtils.isNotBlank(after));

			/*FIXME TEMP TEST*/
			responseMap.put(MAP_USER_FRIENDS, friendsArrayList);


		} catch (FacebookJsonMappingException e) {
			// Looks like this API method didn't really return a list of users
			logger.error("FacebookJsonMappingException\n" + e);
		} catch (FacebookNetworkException e) {
			// An error occurred at the network level
			logger.error("API returned HTTP status code " + e.getHttpStatusCode());
		} catch (FacebookOAuthException e) {
			// Authentication failed - bad access token?
			logger.error("FacebookOAuthException\n" + e);
		} catch (FacebookGraphException e) {
			// The Graph API returned a specific error
			logger.error("Call failed. API says: " + e.getErrorMessage());
		} catch (FacebookResponseStatusException e) {
			// Old-style Facebook error response.
			// The Graph API only throws these when FQL calls fail.
			// You'll see this exception more if you use the Old REST API
			// via LegacyFacebookClient.
			if (e.getErrorCode() == 200)
				logger.error("Permission denied!\n" + e);
			else
				logger.error("FacebookResponseStatusException\n" + e);
		} catch (FacebookException e) {
			// This is the catchall handler for any kind of Facebook exception
			logger.error("FacebookException\n" + e);
		}

		GDBM.processUserResponse(responseMap);
	}
	

	
}
