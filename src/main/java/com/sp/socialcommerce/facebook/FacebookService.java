
package com.sp.socialcommerce.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.*;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.prop.ApplicationProperties;
import com.sp.socialcommerce.prop.Properties;
import com.sp.socialcommerce.recommender.ProductRecommender;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

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

/*	public static final String MAP_PROLONGED_TOKEN = "prolongedToken";*/
	
	@Autowired
	private ApplicationProperties applicationProperties;

    @Qualifier("graphDBManager")
    @Autowired
	private GraphDBManager GDBM;
    @Autowired
    ProductRecommender productRecommender;

    private Set<String> userProcessingSet = new HashSet<>();

    public boolean isProcessingUser(String userId) {
        return userProcessingSet.contains(userId);
    }

	@Async
	public void processUser(String userId, String accessToken) {

        userProcessingSet.add(userId);

		HashMap<String, Object> responseMap = new HashMap<>();

		try {
			logger.info("Inside processUser try...");

			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Properties.FB_APP_SECRET, Version.VERSION_2_4);

			logger.info("FacebookClient: " + facebookClient==null ? null : facebookClient.toString());

			// ==============================
			// USER PROFILE
			// ==============================

			User fbUser = facebookClient.fetchObject("me", User.class,
					Parameter.with("fields", "age_range,hometown,location,political,religion,relationship_status,gender,sports," +
							"favorite_athletes,favorite_teams"));

			if(fbUser != null) {
				responseMap.put(MAP_USER_PROFILE, fbUser);

				if(fbUser.getGender() != null)
					responseMap.put(MAP_USER_GENDER, fbUser.getGender());

				if(fbUser.getHometownName() != null)
					responseMap.put(MAP_USER_HOMETOWN, fbUser.getHometownName());

				if(fbUser.getLocation() != null)
					responseMap.put(MAP_USER_LOCATION, fbUser.getLocation().getName());

				if(fbUser.getPolitical() != null)
					responseMap.put(MAP_USER_POLITICAL, fbUser.getPolitical());

				if(fbUser.getReligion() != null)
					responseMap.put(MAP_USER_RELIGION, fbUser.getReligion());

				logger.info("User profile: " + fbUser.toString());
			} else {
				logger.error("Facebook user is null!");
			}




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
				friendsArrayList.add(friendsArray);

				/*counter += friendsArray.length();*/
				/*logger.info("friends array: " + friendsArray.toString());
				logger.info("count total:" + counter);*/

				logger.info("Searching for 'after'...");

				if(jsonObject.has("paging"))
					if(jsonObject.getJsonObject("paging").has("cursors"))
						after = (String) jsonObject.getJsonObject("paging").getJsonObject("cursors").get("after");
				logger.info("After: " + after);

			} while(StringUtils.isNotBlank(after));

			responseMap.put(MAP_USER_FRIENDS, friendsArrayList);



			// ==============================
			// PROLONG TOKEN
			// ==============================

/*
			JsonObject prolongedToken = facebookClient.fetchObject("oauth/access_token", JsonObject.class,
					Parameter.with("grant_type", "fb_exchange_token"),
					Parameter.with("client_id", Properties.FB_APP_ID),
					Parameter.with("client_secret", Properties.FB_APP_SECRET),
					Parameter.with("fb_exchange_token", accessToken));

			logger.info("prolongedToken: " + prolongedToken.toString());

			if(prolongedToken != null && prolongedToken.has("access_token")) {
				responseMap.put(MAP_PROLONGED_TOKEN, prolongedToken.get("access_token"));
			}
*/

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


        // Recommendations
        /*Map<Product, Double> productsMap = productRecommender.getRecommendedProductsForUser(userId);*/

        userProcessingSet.remove(userId);
	}
	

	
}
