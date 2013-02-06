package controllers;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;

import java.util.Date;
import java.util.Iterator;

import models.Child;
import models.Child.Sex;
import models.Schedule;
import models.User;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Result;

public class ChildrenTests {


		@Test
		public void callAddChild(){
			running(fakeApplication(), new Runnable(){
				@Override
				public void run() {
					User user = User.findOne();
					String name = "Hudson";
	
					JsonNode json = play.libs.Json.toJson(new Child(name, new Date().getTime(),Sex.MALE));
					
					callAction(controllers.routes.ref.Children.addChild(user._id),
							fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
	
					Iterator<String> childIds = User.findOneById(user._id).childIds.iterator();
					
					boolean flag = false;
					while(childIds.hasNext()){
						if(Child.findOneById(childIds.next()).firstName.equals(name)) flag = true;
					}
					assertThat(flag).isTrue();
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
				
				callAction(controllers.routes.ref.Children.updateChild(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
	
	
				user = User.findOneById(user._id);
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
				User user = User.findOne();
				Child child = Child.create(new Child("Hudson", new Date().getTime(),Sex.MALE));
				User.addChild(user._id, child._id);

				JsonNode json = play.libs.Json.toJson(child);	
				
				callAction(controllers.routes.ref.Children.deleteChild(child._id, user._id),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

				Iterator<String> childIds = User.findOneById(user._id).childIds.iterator();
				boolean flag = false;
				while(childIds.hasNext()){
					if(Child.findOneById(childIds.next()).firstName.equals(child.firstName)) flag = true;
				}
				assertThat(flag).isFalse();
				
			}
		});
	}

}
