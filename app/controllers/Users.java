package controllers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;

import org.codehaus.jackson.JsonNode;

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
		JsonNode json = request().body().asJson();
		
		String userName = json.findPath("userNameEmail").getTextValue();
		if(userName==null) return Results.notFound("Missing userNameEmail");
		
		String password = json.findPath("password").getTextValue();
		if(password==null) return Results.notFound("Missing password");

		User user = new User(userName, password);
		String validation = user.validate();
		
		if(validation!= null) return Results.notFound(validation);
		
		User.create(user);
		return redirect(routes.Users.index());
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result deleteUser(){
		JsonNode json = request().body().asJson();
		
		String userName = json.findPath("userNameEmail").getTextValue();
		if(userName==null) return Results.notFound("Missing userNameEmail");
		
		String password = json.findPath("password").getTextValue();
		if(password==null) return Results.notFound("Missing password");

		User user = User.findByUserName(userName);
		if(user==null) return Results.notFound("User "+userName+" not found");
		
		User.delete(user._id);
		return redirect(routes.Users.index());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateUser(){
		JsonNode json = request().body().asJson();
		
		String id = json.findPath("_id").getTextValue();
		if(id==null) return Results.notFound("Missing id");
		
		String password = json.findPath("password").getTextValue();
		if(password==null) return Results.notFound("Missing password");

		User user = User.findOneById(id);
		if(user==null) return Results.notFound("User does not exist");
		if(!password.equals(user.password)) return Results.badRequest("Wrong password");
		
		user.userNameEmail=json.findPath("userNameEmail").getTextValue();
		user.firstName=json.findPath("firstName").getTextValue();
		user.lastName=json.findPath("lastName").getTextValue();
		
		String newPass = json.findPath("newPass").getTextValue();
		if(newPass!=null){
			if(!newPass.isEmpty()) user.password = newPass;
		}
		
		User.update(user);
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