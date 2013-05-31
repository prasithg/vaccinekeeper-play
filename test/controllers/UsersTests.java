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
				
//				Create new user
				User user = new User("bill.gates@microsoft.com","password");
				
//				Test register
				JsonNode json = play.libs.Json.toJson(user);
				
				Result result = callAction(controllers.routes.ref.Users.registerUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

				assertThat(status(result)).isEqualTo(Status.OK);

//				Test repeat register
				result = callAction(controllers.routes.ref.Users.registerUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
				
				assertThat(status(result)).isEqualTo(Status.BAD_REQUEST);
				
			}
		});
	}
	
	@Test
	public void callGetUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				User user = User.findOne();
				Result result = callAction(controllers.routes.ref.Users.getUser(user._id));
				assertThat(play.libs.Json.parse(contentAsString(result)).get("userNameEmail").asText()).isEqualTo(user.userNameEmail);				
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

				JsonNode json = play.libs.Json.toJson(user);

				callAction(controllers.routes.ref.Users.updateUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
				
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
				
				User user = User.findByName("prasith@vaccinekeeper.com");
				
//				TODO: User should have no child references
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
				
				callAction(controllers.routes.ref.Users.deleteUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

//				Attempt to retrieve user and child				
				user = User.findByName("prasith@vaccinekeeper.com");
				
				assertThat(user).isNull();
				assertThat(Child.findOneById(childId)).isNull();

			}
		});
	}
	

	
	



}
