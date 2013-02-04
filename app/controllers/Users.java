package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.MongoException;

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
		Child child = null;
		try{
			child = Child.findOneById(childId);			
		} catch (IllegalArgumentException  e) {
			return Results.notFound("The child id "+childId+" is not valid");
		}
		return ok(play.libs.Json.toJson(child));			
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateSchedule(){
		
		//Capture the request and parse
		JsonNode json = request().body().asJson();
		String childId = json.findPath("childId").getTextValue();
		if(childId==null || childId.isEmpty()) return Results.notFound("Missing childId");

		JsonNode s = json.get("schedule");
		if(s==null) return Results.notFound("Missing schedule");

		ObjectMapper mapper = new ObjectMapper();
		Schedule schedule = null;
		try {
			schedule = mapper.readValue(s, Schedule.class);
		} catch (IOException e) {
			return Results.notFound("schedule has wrong format");
		}
		
		//Get the child
		Child child = null;
		try{
			child = Child.findOneById(childId);			
		} catch (IllegalArgumentException e) {
			return Results.notFound("The child id "+childId+" is not valid");
		}
		
		//Sort through the schedule list
		Iterator<Schedule> list = child.schedule.iterator();
		while(list.hasNext()){
			Schedule sched = list.next();
			if(sched.shortName.equals(schedule.shortName) & sched.shot == schedule.shot){
				if(schedule.lastModified > sched.lastModified){
					sched.cancelled = schedule.cancelled;
					sched.complete = schedule.complete;
					sched.comment = schedule.comment;
					sched.lastModified = schedule.lastModified;
					sched.scheduledDate = schedule.scheduledDate;
				}
			break;
			}
		}
		
		//Persist and send result
		Child.update(child);
		return redirect(routes.Users.getChild(childId));
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