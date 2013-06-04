package actions;

import models.LoginUser;
import models.User;
import controllers.Application;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Login Interceptor that will make sure the request is authenticated
 * 
 * @author prasithg
 * 
 *
public class LoginAction extends Action.Simple {

	public Result call(Http.Context ctx) throws Throwable {
		
		User user = null;
		
		Logger.info("Calling login action test for " + ctx);
		
		
		// get authtoken from the headers
		String authToken = ctx.request().getHeader("authToken");
		
		Logger.info("AuthToken from header is: " + authToken);
		
		// check if the authtoken is in the cache
		LoginUser login = Application.cache.getIfPresent(authToken);
		
		if (login==null){
			//User is not authenticated or token wasn't passed in
			return Results.unauthorized();
		} else {
			// look up user from db using username from login object
			Logger.info("Got the Login object from the cache: " + login.userNameEmail);
			user = User.findByName(login.userNameEmail);
		}
		
		// If user is authenticated place User object in body so all methods can retrieve it
		// Methods have no idea about authtoken and deal only with the user object
		ctx.args.put("user", user);
		
		return delegate.call(ctx);
	}
}
*/