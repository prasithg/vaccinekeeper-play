package controllers;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import models.Child;
import models.Family;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Content;
import play.mvc.Http.Status;
import play.mvc.Result;

public class UsersTests {


	//Test register user routing
	@Test
	public void callRegisterUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
//				Delete existing user if he exists
				User user = User.findByName("bill.gates@microsoft.com");
				if(user!= null) User.delete(user._id);
				
//				Create new user
				user = new User("bill.gates@microsoft.com","password");
				
//				Test register
				JsonNode json = play.libs.Json.toJson(user);
				
				Result result = callAction(controllers.routes.ref.Users.registerUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

				assertThat(status(result)).isEqualTo(Status.SEE_OTHER);

//				Test repeat register
				result = callAction(controllers.routes.ref.Users.registerUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
				
				assertThat(status(result)).isEqualTo(Status.BAD_REQUEST);
				
			}
		});
	}
	
	@Test
	public void callUpdateUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
				User user = User.findOne();
				String newEmail = "newEmail@gmail.com";
				user.userNameEmail = newEmail;

				JsonNode node = play.libs.Json.toJson(user);

				routeAndCall(fakeRequest(POST, "/update")
					.withJsonBody(node));
				
				user = User.findByName(newEmail);
				assertThat(user).isNotNull();
				
				user.userNameEmail = "michael@vaccinekeeper.com";
				User.update(user);

			}
		});
	}
	
	
	@Test
	public void callDeleteUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
//				Ensure user Exists
				User user = User.findByName("prasith@vaccinekeeper.com");
				if(user==null){
					user = new User("prasith@vaccinekeeper.com", "password");
					user = User.create(user);					
				}
				
//				Ensure user has children
				if(user.childIds==null){
					Calendar dob = Calendar.getInstance();
					dob.set(2009, 8, 25);
					Child child = Child.create(new Child("Samina", dob.getTimeInMillis(), Child.Sex.FEMALE));
					
					User.addChild(user._id, child._id);
					user = User.findOneById(user._id);
				}
				
//				Get reference for one child
				String childId = user.childIds.iterator().next();			
				
//				Build json and send
				JsonNode json = play.libs.Json.toJson(user);
				
				routeAndCall(fakeRequest(POST, "/delete")
					.withJsonBody(json));

//				Attempt to retrieve user and child				
				user = User.findByName("prasith@vaccinekeeper.com");
				
				assertThat(user).isNull();
				assertThat(Child.findOneById(childId)).isNull();

			}
		});
	}
	

	
	



}
