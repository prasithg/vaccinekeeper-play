package controllers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;
import models.Vaccine;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static Result index() {
		
		return ok(views.html.index.render(getFamilies()));
	}

	public static Result genericSchedule() {
		return ok(play.libs.Json.toJson(Vaccine.all()));
	}

	public static Result getChild(String childId){
		return ok(play.libs.Json.toJson(Child.findOne(childId)));
		
	}

	private static List<Family> getFamilies(){
		Iterator<User> users = User.all().iterator();
		List<Family> families = new LinkedList<Family>();
		
		while(users.hasNext()){
			User user = users.next();
			List<Child> children = new LinkedList<Child>();
			Iterator<String> childIds = user.childIds.iterator();
			while(childIds.hasNext()){
				children.add(Child.findOne(childIds.next()));
			}
			Family family = new Family(user, children);
			families.add(family);
		}
		return families;
	}
	
}