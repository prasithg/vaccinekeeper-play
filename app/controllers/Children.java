package controllers;

import java.io.IOException;
import java.util.Iterator;

import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;

import models.Child;
import models.Child.Sex;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


public class Children extends Controller {

	public static Result getChild(String childId){
		Child child = Child.findOneById(childId);
		if (child == null ) return Results.notFound("The child id "+childId+" is not valid");
		return ok(play.libs.Json.toJson(child));			
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result test(){
		ObjectMapper mapper = new ObjectMapper();
				
		Schedule schedule = null;
		try {
			schedule = mapper.readValue(request().body().asJson(), Schedule.class);
		} catch (IOException e) {
			return Results.notFound("json is not of format Schedule");
		}
		
		String validate = schedule.validate();
		if(validate!= null) return Results.badRequest(validate);
				
		
		return ok(play.libs.Json.toJson(schedule));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addChild(String _id) {
		
//		Assume user is logged in - don't need to find user or validate passwords
		
//		Map child object
		ObjectMapper mapper = new ObjectMapper();
		Child child = null;
		try {
			child = mapper.readValue(request().body().asJson(), Child.class);
		} catch (IOException e) {
			return Results.notFound("json is not of format Child");
		}

//		Validate
		String val = child.validateNew();
		if(val!= null) return Results.badRequest(val);

//		Persist
		child = new Child(child.firstName, child.dob, child.sex);
		child = Child.create(child);
		User.addChild(_id, child._id);
		
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