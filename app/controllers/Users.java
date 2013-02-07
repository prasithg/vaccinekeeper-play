package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;

import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


public class Users extends Controller {

	public static Result index() {
		return ok(views.html.index.render(getFamilies()));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result registerUser(){

//		Map user object
		User user = null;
		try {
			user = new ObjectMapper().readValue(request().body().asJson(), User.class);
			user.dateCreated = new Date().getTime();
		} catch (IOException e) {
			return Results.notFound("json is not of format User");
		}

//		Validate
		String val = user.validateNew();
		if(val!= null) return Results.badRequest(val);

//		Persist
		user = User.create(user);		

		return ok(play.libs.Json.toJson(user));	
	}
	
	public static Result getUser(String _id){
		User user = User.findOneById(_id);
		if (user == null ) return Results.notFound("The user id "+_id+" is not valid");

		return ok(play.libs.Json.toJson(user));	
	}
	
	
	@BodyParser.Of(BodyParser.Json.class)
		public static Result updateUser(){
	
	//		Retrieve user from request
			User user = null;
			try {
				user = new ObjectMapper().readValue(request().body().asJson(), User.class);
			} catch (IOException e) {
				return Results.notFound("json is not of format User");
			}
	
	//		Retrieve user from DB 
			User dbUser = User.findOneById(user._id);
			if(dbUser==null) return Results.notFound("User id does not exist");
			
	//		Update details
			String update = dbUser.updateDetails(user);
			if(update!=null)
				return Results.badRequest(update);
			user = User.update(dbUser);
			
			return ok(play.libs.Json.toJson(user));	
		}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result deleteUser(){
		
		User user = null;
		try {
			user = new ObjectMapper().readValue(request().body().asJson(), User.class);
		} catch (IOException e) {
			return Results.notFound("json is not of format User");
		}

//		I know user is logged in but going to double check the password
		String val = user.validateExisting();
		if(val!= null) return Results.badRequest(val);
		
//		Remove from db
		User.delete(user._id);
		
		return redirect(routes.Users.index());
	}

	private static List<Family> getFamilies(){
		Iterator<User> users = User.all().iterator();
		List<Family> families = new LinkedList<Family>();
		
		while(users.hasNext()){
			User user = users.next();
			List<Child> children = new LinkedList<Child>();
			Iterator<String> childIds = new LinkedList<String>().iterator();
			if(user.childIds != null) childIds = user.childIds.iterator();
			while(childIds.hasNext()){
				Child child = Child.findOneById(childIds.next());
				if (child!=null) children.add(child);
			}
			Family family = new Family(user, children);
			families.add(family);
		}
		return families;
	}
}