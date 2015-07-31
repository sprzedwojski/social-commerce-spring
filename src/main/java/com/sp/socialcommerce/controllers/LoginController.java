package com.sp.socialcommerce.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.*;
import com.sp.socialcommerce.gigya.GigyaService;
import com.sp.socialcommerce.models.User;
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

	@Autowired
	private GigyaService gigyaService;

    /*@Autowired*/
    /*private Facebook facebook;

    @Inject
    public LoginController(Facebook facebook) {
        this.facebook = facebook;
    }*/

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
		/*model.addAttribute("sitename", Properties.GIGYA_SITENAME);
		model.addAttribute("apikey", Properties.GIGYA_API_KEY);*/

		return "login";

/*        if (!facebook.isAuthorized()) {
            return "redirect:/connect/facebook";
        }

        model.addAttribute(facebook.userOperations().getUserProfile());
        PagedList<Post> homeFeed = facebook.feedOperations().getHomeFeed();
        model.addAttribute("feed", homeFeed);

        return "hello";*/
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String homeSubmit(@ModelAttribute User user, Locale locale, Model model, HttpServletRequest request) {
		model.addAttribute("user", user);		
		logger.info("The client access token is {}.", user.getAccessToken());

		request.getSession().setAttribute("uid", user.getAccessToken());

        System.setProperty("http.proxyHost", "w3cache.amg.net.pl");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "w3cache.amg.net.pl");
        System.setProperty("https.proxyPort", "8080");

        try {
            FacebookClient facebookClient = new DefaultFacebookClient(user.getAccessToken(), "0fe684088057ab1512e6402934dac8fa"/*"9816fe1d0cbd35d586c4bb0e94d581ab"*//*APP SECRET*/, Version.VERSION_2_4);

            com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);

            logger.info("User name: " + fbUser.getName());
            logger.info("User political: " + fbUser.getPolitical());
            logger.info("User religion:" + fbUser.getReligion());

            com.restfb.types.Likes likes = facebookClient.fetchObject("me/likes", com.restfb.types.Likes.class, Parameter.with("limit", "5000"));

            logger.info("Likes: " + likes.toString());

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

		/*gigyaService.processUser(user.getUID());*/
		return "redirect:survey_intro";
	}

	@RequestMapping(value = "/login/test", method = RequestMethod.POST)
	public void testUser(String uid){
		gigyaService.processUser(uid);
	}
	
}