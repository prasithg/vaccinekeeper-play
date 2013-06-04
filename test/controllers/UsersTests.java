package controllers;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

import net.vz.mongodb.jackson.DBCursor;

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
				assertThat(play.libs.Json.parse(contentAsString(result)).get("userNameEmail").asText()).isEqualTo(user.email);				
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
				user.email = newEmail;

				JsonNode json = play.libs.Json.toJson(user);

				callAction(controllers.routes.ref.Users.updateUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
				
				user = User.findByName(newEmail);
				assertThat(user).isNotNull();
				
				user.email = "michael@vaccinekeeper.com";
				User.update(user);

			}
		});
	}
	
	
	@Test
	public void callDeleteUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
				User user = User.findOne();
				String userId = user._id;
				
//				Build Json and send
				JsonNode json = play.libs.Json.toJson(user);
				
				callAction(controllers.routes.ref.Users.deleteUser(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

//				Attempt to retrieve user and child				
				user = User.findOneById(userId);
				
				assertThat(user).isNull();
				
				
//				assertThat(Child.getChildren(userId).toArray().size()).isEqualTo(0);

			}
		});
	}
	

	
	



}
