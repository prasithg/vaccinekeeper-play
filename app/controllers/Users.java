package controllers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;

import org.codehaus.jackson.JsonNode;

import play.data.validation.Constraints.EmailValidator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.BodyParser;                     


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