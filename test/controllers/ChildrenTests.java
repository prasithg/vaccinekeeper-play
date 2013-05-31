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
					int childCount = Child.getChildren(user._id).size();
					
					String name = "Hudson";
	
					JsonNode json = play.libs.Json.toJson(new Child(user._id, name, new Date().getTime(),Sex.MALE));
					
					callAction(controllers.routes.ref.Children.addChild(),
							fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
	
					assertThat(Child.getChildren(user._id).size()).isEqualTo(childCount+1);
				}
			});
		}



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
				
				Child child = Child.findOne();
				String childId = child._id;
				String name = "Dougy";
				child.firstName = name;
				
				JsonNode json = play.libs.Json.toJson(child);
				
				callAction(controllers.routes.ref.Children.updateChild(),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));
	
				child = Child.findOneById(childId);
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
				Child child = Child.create(new Child(user._id, "Hudson", new Date().getTime(),Sex.MALE));
				String childId = child._id;
				
				JsonNode json = play.libs.Json.toJson(child);	
				
				callAction(controllers.routes.ref.Children.deleteChild(child._id),
						fakeRequest().withHeader("Content-Type", "application/json").withJsonBody(json));

				assertThat(Child.findOneById(childId)).isNull();
				
			}
		});
	}
	
	@Test
	public void callGetChildren(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				User user = User.findOne();
				int childCount = Child.getChildren(user._id).size();
				assertThat(childCount).isEqualTo(2);
				
				Result result = callAction(controllers.routes.ref.Children.getChildren(user._id));
				assertThat(play.libs.Json.parse(contentAsString(result)).size()).isEqualTo(childCount);
				
			}
		});
	}


}
