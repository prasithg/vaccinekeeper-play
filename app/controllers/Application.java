package controllers;

import java.util.List;

import models.User;
import models.Vaccine;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static Result index() {
		return ok(views.html.index.render(User.all()));
	}

	public static Result genericSchedule() {
		return ok(play.libs.Json.toJson(Vaccine.all()));
	}

	public static Result getChild(String childId){
		
//		Jackson doesn't like this
//		Child.findOne(childId);
		
		
//		This works
		return ok(play.libs.Json.toJson("The route works but that's it"));
		
	}

	
	
}