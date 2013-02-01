package controllers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;

public class Users extends Controller {

	public static Result index() {
		return ok(views.html.index.render(getFamilies()));
	}

	public static Result registerUser(){
		JsonNode node = request().body().asJson();
		String userNameEmail = node.get("userNameEmail").asText();
		User user = User.findByUserName(userNameEmail);

		//If user exists send an error
		if(user!=null) return badRequest("A user with the email "+userNameEmail+" already exists");
		
		user = new User(userNameEmail, node.get("password").asText());
		User.create(user);
		return redirect(routes.Users.index());
	}
	
	public static Result getChild(String childId){
		return ok(play.libs.Json.toJson(Child.findOneById(childId)));
	}
	
	public static Result updateSchedule(){
		return TODO;
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
				children.add(Child.findOneById(childIds.next()));
			}
			Family family = new Family(user, children);
			families.add(family);
		}
		return families;
	}
}