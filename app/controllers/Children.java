package controllers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

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

	public static Result getChildren(String userId){
		return TODO;
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addChild(String userId) {
		
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
//		TODO: This should just take a userId and then you don't have to run the User.addChild method
		child = new Child(child.firstName, child.dob, child.sex);
		child = Child.create(child);		
		User.addChild(userId, child._id);
		
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

//		Validate
		String val = child.validateExisting();
		if(val!= null) return Results.badRequest(val);
		
//		Update and persist
		Child dbChild = Child.findOneById(child._id);
		dbChild.updateDetails(child);
		child = Child.update(dbChild);
		
		return ok(play.libs.Json.toJson(child));			
	}

	
	@BodyParser.Of(BodyParser.Json.class)
		public static Result updateSchedule(String _id){
			
	//		Assume user is logged in - don't need to find user or validate passwords
	
	//		Get the child
			Child child = Child.findOneById(_id);
			if(child==null) return Results.notFound("The child id "+_id+" is not valid");
			
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

	public static Result deleteChild(String childId, String userId) {
//		TODO: Update this so that you can delete a child without touching a user
		
//		Assume user is logged in - don't need to find user or validate passwords
		User user = User.findOneById(userId);
		Iterator<String> childIds = new LinkedList<String>().iterator();
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
		
//		Delete child and remove from user list
		Child.delete(childId);
		user.childIds.remove(childId);
		User.update(user);
		
		return redirect(routes.Users.getUser(userId));
	}
	
}