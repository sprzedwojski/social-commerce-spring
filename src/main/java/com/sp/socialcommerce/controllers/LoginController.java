package com.sp.socialcommerce.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.*;
import com.restfb.json.JsonObject;
import com.sp.socialcommerce.facebook.FacebookService;
import com.sp.socialcommerce.gigya.GigyaService;
import com.sp.socialcommerce.models.User;
import com.sp.socialcommerce.neo4j.GraphDBManager;
import com.sp.socialcommerce.prop.Properties;
import com.sp.socialcommerce.prop.PropertiesConstants;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import scala.xml.PrettyPrinter;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Handles requests for the application home page.
 */
@Controller
@EnableAsync
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	/*@Autowired
	private GigyaService gigyaService;*/

    @Autowired
    private FacebookService facebookService;

    @Autowired
    private GraphDBManager GDBM;

    /**
     * Simply selects the home view to render by returning its name.
     */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(Locale locale, Model model, HttpServletRequest request) {

		// If user id is present in the session we will not log him in again, but redirect directly to the survey
		if(request.getSession().getAttribute(FacebookService.USER_ID) != null
                && request.getSession().getAttribute(FacebookService.USER_ACCESS_TOKEN) != null) {
			return "redirect:survey_intro";
		}

		model.addAttribute("user", new User() );
        model.addAttribute("fbAppId", Properties.FB_APP_ID);

		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model, HttpServletRequest request) {
		model.addAttribute("user", user);

        String accessToken = null, userId = null;
        accessToken = user.getAccessToken();

		logger.info("The client access token is {}.", accessToken);

        /*System.setProperty("http.proxyHost", "w3cache.amg.net.pl");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "w3cache.amg.net.pl");
        System.setProperty("https.proxyPort", "8080");*/

        com.restfb.types.User fbUser = null;
        String prolongedToken = null;

        try {

            FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Properties.FB_APP_SECRET, Version.VERSION_2_4);
            fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);

            // ==============================
            // PROLONG TOKEN
            // ==============================

            JsonObject prolongedTokenObject = facebookClient.fetchObject("oauth/access_token", JsonObject.class,
                    Parameter.with("grant_type", "fb_exchange_token"),
                    Parameter.with("client_id", Properties.FB_APP_ID),
                    Parameter.with("client_secret", Properties.FB_APP_SECRET),
                    Parameter.with("fb_exchange_token", accessToken));

            logger.info("prolongedToken: " + prolongedTokenObject.toString());

            if(prolongedTokenObject != null && prolongedTokenObject.has("access_token")) {
                /*responseMap.put(MAP_PROLONGED_TOKEN, prolongedTokenObject.get("access_token"));*/
                prolongedToken = (String) prolongedTokenObject.get("access_token");
            }

        } catch (FacebookJsonMappingException e) {
            // Looks like this API method didn't really return a list of users
        } catch (FacebookNetworkException e) {
            // An error occurred at the network level
            System.out.println("API returned HTTP status code " + e.getHttpStatusCode());
        } catch (FacebookOAuthException e) {
            // Authentication failed - bad access token?
        } catch (FacebookGraphException e) {
            // The Graph API returned a specific error
            System.out.println("Call failed. API says: " + e.getErrorMessage());
        } catch (FacebookResponseStatusException e) {
            // Old-style Facebook error response.
            // The Graph API only throws these when FQL calls fail.
            // You'll see this exception more if you use the Old REST API
            // via LegacyFacebookClient.
            if (e.getErrorCode() == 200)
                System.out.println("Permission denied!");
        } catch (FacebookException e) {
            // This is the catchall handler for any kind of Facebook exception
        }

        if(fbUser != null) {
            userId = fbUser.getId();

            logger.info("User name: " + fbUser.getName());
            logger.info("User ID: " + userId);

            Node userNode = GDBM.getUserNode(userId, fbUser.getName(), prolongedToken != null ? prolongedToken : null);
            if(userNode != null) {
                logger.info("User node created successfully.");

                request.getSession().setAttribute(FacebookService.USER_ACCESS_TOKEN, prolongedToken != null ? prolongedToken : accessToken);
                request.getSession().setAttribute(FacebookService.USER_ID, userId);

                facebookService.processUser(userId, accessToken);
                return "redirect:survey_intro";
            }
            else
                logger.error("Error creating user node!");
        } else {
            logger.error("Error fetching user info from Facebook!");
        }

        // TODO Set error status.
        return "login";
	}
	
}