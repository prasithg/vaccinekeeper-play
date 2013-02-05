package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Child.Sex;
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


public class Children extends Controller {

	public static Result getChild(String childId){
		Child child = Child.findOneById(childId);
		if (child == null ) return Results.notFound("The child id "+childId+" is not valid");
		return ok(play.libs.Json.toJson(child));			
	}

/*	@BodyParser.Of(BodyParser.Json.class)
	public static Result addChild(){
		//userId, password, { firstName, dob,  sex }
		//Capture the request and parse
		JsonNode json = request().body().asJson();
		
//		Validate user
		String id = json.findPath("_id").getTextValue();
		if(id==null) return Results.notFound("Missing userNameEmail");
		
		String password = json.findPath("password").getTextValue();
		if(password==null) return Results.notFound("Missing password");

		User user = User.findOneById(id);
		if(user==null) return Results.notFound("User does not exist");
		if(!password.equals(user.password)) return Results.badRequest("Wrong password");

//		Validate child
		String name = json.findPath("firstName").getTextValue();
		if(name==null) return Results.notFound("Missing child's name");
		
		long dob = json.findPath("dob").getLongValue();
		if(dob==0) return Results.notFound("Missing date of birth");
		
		Sex sex = null;
		try{
			sex = Sex.valueOf(json.findPath("sex").getTextValue());
		} catch (NullPointerException | IllegalArgumentException f ){
			return Results.notFound("Missing sex");
		}
		
//		Create child and validate some more
		Child child = new Child(name, dob, sex);
		String validation = child.validate();
		if(validation!= null) return Results.badRequest(validation);

//		Persist and send result
		child = Child.create(child);
//		User.addChild(user._id, child._id);
//		
		return redirect(routes.Children.getChild(child._id));		
	}
*/
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addChild(){
		JsonNode json = request().body().asJson();
		
//		Validate user
		String id = json.findPath("_id").getTextValue();
		if(id==null) return Results.notFound("Missing userNameEmail");
		
		String password = json.findPath("password").getTextValue();
		if(password==null) return Results.notFound("Missing password");

		User user = User.findOneById(id);
		if(user==null) return Results.notFound("User does not exist");
		if(!password.equals(user.password)) return Results.badRequest("Wrong password");

//		Validate child
		String name = json.findPath("firstName").getTextValue();
		if(name==null) return Results.notFound("Missing child's name");
		
		long dob = json.findPath("dob").getLongValue();
		if(dob==0) return Results.notFound("Missing date of birth");
		
//		Had this catch (NullPointerException | IllegalArgumentException f ) but was giving me errors
		Sex sex = null;
		try {
			sex = Sex.valueOf(json.findPath("sex").getTextValue());			
		} catch (NullPointerException e) {
			return Results.notFound("Missing sex field");
		} catch (IllegalArgumentException f) {
			return Results.notFound("Incorrect sex");
		}
		
//		Create child and validate some more
		Child child = new Child(name, dob, sex);
		String validation = child.validate();
		if(validation!= null) return Results.badRequest(validation);

//		Persist and send result
		child = Child.create(child);
		User.addChild(user._id, child._id);
		
		return redirect(routes.Children.getChild(child._id));		
	}

	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateSchedule(){
		
		//Capture the request and parse
		JsonNode json = request().body().asJson();
		String childId = json.findPath("_id").getTextValue();
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
		Child child = Child.findOneById(childId);
		if(child==null) return Results.notFound("The child id "+childId+" is not valid");
		
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
		return redirect(routes.Children.getChild(childId));
	}
	
}