package controllers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import models.Child;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


public class Children extends Controller {

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
	
	public static Result getChild(String childId){
		Child child = Child.findOneById(childId);
		if (child == null ) return Results.notFound("The child id "+childId+" is not valid");
		return ok(play.libs.Json.toJson(child));			
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result editChild() {
		
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
		String val = child.validateExisting();
		if(val!= null) return Results.badRequest(val);
		
//		Update and persist
		Child dbChild = Child.findOneById(child._id);
		dbChild.updateDetails(child);
		Child.update(dbChild);
		
		return redirect(routes.Children.getChild(child._id));		
	}

	
	public static Result deleteChild(String childId, String userId) {
		
//		Assume user is logged in - don't need to find user or validate passwords
			
		User user = User.findOneById(userId);
		Iterator<String> childIds = null;
		try{
			childIds = user.childIds.iterator();
		} catch (NullPointerException e){
			return Results.notFound("User does not have children");
		}
		
//		Don't need intense verification but confirm the childId belongs to the parent
		boolean notThere = true;
		while(childIds.hasNext()){
			if(childIds.next().equals(childId))
				notThere = false;
		}
		if(notThere) return Results.notFound("Child id does not belong to user");
		
//		Delete
		Child.delete(childId);
		
		return redirect(routes.Users.index());
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