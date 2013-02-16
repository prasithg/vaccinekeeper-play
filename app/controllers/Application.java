package controllers;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import actions.LoginAction;
import models.AppStatus;
import models.LoginUser;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Cache;;

public class Application extends Controller {

	public static String projectToken = "VaccineClipboard";
	
	//Session timeout - based on last accessed time
	public static int sessionTimeoutMins = 20;
	
	//This creates a cache object where we will store our authtokens
	public static Cache<String, LoginUser> cache =
			   CacheBuilder.newBuilder()
			     .expireAfterAccess(sessionTimeoutMins, TimeUnit.MINUTES).build();

	public static Result index() {
		return ok(views.html.index.render());
	}

	public static Result pingStatus() {
		// TODO Ping DB to make sure its up and update status accordingly
		return ok(play.libs.Json.toJson(new AppStatus()));
	}

	public static Result loginUser(String userNameEmail, String password) {

		User user = new User(userNameEmail, password);

		// Check if the user/pwd is valid using crappy way
		String val = user.validateExisting();
		if (val != null)
			return Results.unauthorized(val);

		// Generate Token for user with token, name and time for uniqueness
		String key = UUID.randomUUID().toString().toUpperCase() + "|"
				+ projectToken + "|" + userNameEmail + "|"
				+ System.currentTimeMillis();

		// String Ecryptor from Jasypt
		StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();

		// this is the authentication token that must be sent with every request
		String authToken = jasypt.encrypt(key);

		// Create Login Model
		LoginUser login = new LoginUser(userNameEmail, authToken);
		
		// Store token into Guava Cache
		cache.put(authToken, login);
		
		//Log out the cache just for fun
		Logger.info(cache.toString());

		// Return Login Model Object so client can get authToken from it
		return ok(play.libs.Json.toJson(login));
	}

	@With(LoginAction.class)
	public static Result testLoginAction() {
		// TODO retrieve User Object from body
		// User user = null;
		// try {
		// user = new ObjectMapper().readValue(request().body().asJson(),
		// User.class);
		// } catch (IOException e) {
		// Logger.error("Gaaah something is wrong");
		// }

		// TODO return User object for the user we are authenticated as

		return ok("Called Login Action");
	}

}