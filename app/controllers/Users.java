package controllers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import models.Child;
import models.Family;
import models.User;
import models.Vaccine;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;

public class Users extends Controller {

	public static Result index() {
		return ok(views.html.index.render(getFamilies()));
	}

	public static Result registerUser(){
		//Get request and node
		JsonNode node = request().body().asJson();
		String userNameEmail = node.get("userNameEmail").asText();
		
		//Test if userNameEmail exists in database
		
		
		
		
		//Create user OR
		
		User user = new User(userNameEmail, node.get("password").asText());
		
		
		
		//Redirect user
		
		
		User.create(user);
		return redirect(routes.Users.index());
	}
	
	public static Result getChild(String childId){
		return ok(play.libs.Json.toJson(Child.findOneById(childId)));
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