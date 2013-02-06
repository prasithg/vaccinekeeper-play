package controllers;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.POST;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;

import java.util.Date;

import models.Child;
import models.Child.Sex;
import models.Schedule;
import models.User;
import models.Vaccine;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Result;

public class ChildrenTests {


	//	Test suspended, don't know how to add url parameters to the FakeRequest
	//	@Test
		public void callAddChild(){
			running(fakeApplication(), new Runnable(){
				@Override
				public void run() {
					User user = User.findOne();
					String name = "Douglas";
	
					JsonNode json = play.libs.Json.toJson(new Child(name, new Date().getTime(),Sex.MALE));
					
	//				Adding parameters like this "/child?id="+user._id does not work
					Result result = routeAndCall(fakeRequest(POST, "/child")
						.withHeader("Content-Type", "application/json")
						.withJsonBody(json));
	
					System.out.println(play.test.Helpers.contentAsString(result));
	//				Child child = Child.findOneById(child._id);
	//				assertThat(child.firstName).isEqualTo(name);
				}
			});
		}



	//Test getChild
	@Test
	public void callGetChild(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Child child = Child.findOne();
				Result result = callAction(controllers.routes.ref.Children.getChild(child._id));
				assertThat(play.libs.Json.parse(contentAsString(result)).get("firstName").asText()).isEqualTo(child.firstName);
				
			}
		});
	}

	
	
	@Test
	public void callUpdateChild(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				User user = User.findOne();
				Child child = Child.findOneById(user.childIds.get(1));
				String name = "Dougy";
				child.firstName = name;
				
				JsonNode json = play.libs.Json.toJson(child);
				
				Result result = routeAndCall(fakeRequest(POST, "/editChild")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(json));
	
	
				child = Child.findOneById(user.childIds.get(1));
				assertThat(child.firstName).isEqualTo(name);
			}
		});
	}



	//Would be good to simply callAction on the controller but I'm not sure how to pass in variables with the current setup
	@Test
	public void callUpdateSchedule(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Child child = Child.findOne();
				
				int index = 5;
				Schedule schedule = child.schedule.get(index);
				schedule.cancelled = false;
				schedule.complete = true;
				schedule.comment = RandomStringUtils.randomAlphabetic(5);
				schedule.lastModified = child.schedule.get(index).lastModified+10;
				
				JsonNode json = play.libs.Json.toJson(schedule);				

				callAction(controllers.routes.ref.Children.updateSchedule(child._id),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

				child = Child.findOneById(child._id);
				assertThat(child.schedule.get(index).comment).isEqualTo(schedule.comment);
			}
		});
	}
	
	
	
@Test
	public void callDeleteChild(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
			}
		});
	}

}
