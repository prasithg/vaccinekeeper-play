package controllers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import models.Child;
import models.Schedule;
import models.User;
import net.vz.mongodb.jackson.DBCursor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


public class Children extends Controller {

	public static Result getChildren(String userId){
		return ok(play.libs.Json.toJson(Child.getChildren(userId).toArray()));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addChild() {
		
//		Assume user is logged in - don't need to find user or validate passwords
			
//		Map child object
		Child child = null;
		try {
			child = new ObjectMapper().readValue(request().body().asJson(), Child.class);
		} catch (IOException e) {
			return Results.notFound("json is not of format Child");
		}

//		Validate
		String val = child.validateNew();
		if(val!= null) return Results.badRequest(val);

//		Persist
		child = new Child(child.userId, child.firstName, child.dob, child.sex);
		child = Child.create(child);		
		
		return ok(play.libs.Json.toJson(child));			
	}
	
	public static Result getChild(String _id){
		Child child = Child.findOneById(_id);
		if (child == null ) return Results.notFound("The child id "+_id+" is not valid");
		return ok(play.libs.Json.toJson(child));			
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateChild() {
		
//		Assume user is logged in - don't need to find user or validate passwords
			
//		Map child object
		Child child = null;
		try {
			child =  new ObjectMapper().readValue(request().body().asJson(), Child.class);
		} catch (IOException e) {
			return Results.notFound("json is not of format Child");
		}

//		Update and persist
		Child dbChild = Child.findOneById(child._id);
		if(dbChild==null) return Results.badRequest("childId does not exist");
		
		dbChild.updateDetails(child);
		child = Child.update(dbChild);
		
		return ok(play.libs.Json.toJson(child));			
	}

	
	@BodyParser.Of(BodyParser.Json.class)
		public static Result updateSchedule(String childId){
			
	//		Assume user is logged in - don't need to find user or validate passwords
	
	//		Get the child
			Child child = Child.findOneById(childId);
			if(child==null) return Results.notFound("The child id "+childId+" is not valid");
			
	//		Get the schedule from JSON
			Schedule schedule = null;
			try {
				schedule = new ObjectMapper().readValue(request().body().asJson(), Schedule.class);
			} catch (IOException e) {
				return Results.notFound("schedule has wrong format");
			}
			
	//		Sort through the child's schedule list
			Iterator<Schedule> list = child.schedule.iterator();
			while(list.hasNext()){
				Schedule dbSchedule = list.next();
				if(dbSchedule.shortName.equals(schedule.shortName) & dbSchedule.shot == schedule.shot){
					if(dbSchedule.updateDetails(schedule))
						break;					
				}
			}
			
			//Persist and send result
			child = Child.update(child);
			return ok(play.libs.Json.toJson(child));			
		}

	public static Result deleteChild(String childId) {
		
//		Delete child and remove from user list
		boolean deleted = Child.delete(childId);
		
		if(deleted) return Results.ok();
		else return Results.badRequest();
		
	}
	
}