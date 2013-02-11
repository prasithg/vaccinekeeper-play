package models;
/**
 * Object that's passed back to client after user is authenticated.
 * Prevents password from ever being sent back to user.  User only holds an auth token.
 * 
 * @author prasithg
 *
 */
public class LoginUser {
	
	public String userNameEmail;
	public String authToken;
	
	public LoginUser(){	
	}

	public LoginUser(String userNameEmail, String authToken){	
		this.userNameEmail = userNameEmail;
		this.authToken = authToken;
	}

}
